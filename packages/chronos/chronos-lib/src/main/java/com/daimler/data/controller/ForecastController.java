package com.daimler.data.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.daimler.data.api.forecast.ForecastInputsApi;
import com.daimler.data.api.forecast.ForecastProjectsApi;
import com.daimler.data.api.forecast.ForecastRunsApi;
import com.daimler.data.application.auth.UserStore;
import com.daimler.data.controller.exceptions.GenericMessage;
import com.daimler.data.controller.exceptions.MessageDescription;
import com.daimler.data.dto.forecast.CollaboratorVO;
import com.daimler.data.dto.forecast.CreatedByVO;
import com.daimler.data.dto.forecast.ForecastCollectionVO;
import com.daimler.data.dto.forecast.ForecastProjectCreateRequestVO;
import com.daimler.data.dto.forecast.ForecastProjectCreateRequestWrapperVO;
import com.daimler.data.dto.forecast.ForecastProjectResponseVO;
import com.daimler.data.dto.forecast.ForecastRunCollectionVO;
import com.daimler.data.dto.forecast.ForecastRunResponseVO;
import com.daimler.data.dto.forecast.ForecastVO;
import com.daimler.data.dto.forecast.InputFileVO;
import com.daimler.data.dto.forecast.InputFilesCollectionVO;
import com.daimler.data.dto.forecast.RunVO;
import com.daimler.data.dto.forecast.RunVisualizationVO;
import com.daimler.data.dto.storage.FileUploadResponseDto;
import com.daimler.data.service.forecast.ForecastService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@RestController
@Api(value = "Forecast APIs")
@RequestMapping("/api")
@Slf4j
public class ForecastController implements ForecastRunsApi, ForecastProjectsApi, ForecastInputsApi {

	@Autowired
	private ForecastService service;
	
	@Autowired
	private UserStore userStore;
	
	private static final String BUCKETS_PREFIX = "chronos-";
	
	private static final List<String> contentTypes = Arrays.asList("xlsx", "csv");

	private boolean isValidAttachment(String fileName) {
		boolean isValid = false;
		String extension = FilenameUtils.getExtension(fileName);
		if (contentTypes.contains(extension.toLowerCase())) {
			isValid = true;
		}
		return isValid;
	}
	
	@Override
	@ApiOperation(value = "Get list of saved input files", nickname = "getInputFiles", notes = "Get list of saved input files", response = InputFilesCollectionVO.class, tags={ "forecast-inputs", })
    @ApiResponses(value = { 
        @ApiResponse(code = 201, message = "Returns message of success or failure", response = InputFilesCollectionVO.class),
        @ApiResponse(code = 204, message = "Fetch complete, no content found."),
        @ApiResponse(code = 400, message = "Bad request."),
        @ApiResponse(code = 401, message = "Request does not have sufficient credentials."),
        @ApiResponse(code = 403, message = "Request is not authorized."),
        @ApiResponse(code = 405, message = "Method not allowed"),
        @ApiResponse(code = 500, message = "Internal error") })
    @RequestMapping(value = "/forecasts/{id}/inputs",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.GET)
    public ResponseEntity<InputFilesCollectionVO> getInputFiles(@ApiParam(value = "forecast project ID ",required=true) @PathVariable("id") String id){
		InputFilesCollectionVO collection = new InputFilesCollectionVO();
		ForecastVO existingForecast = service.getById(id);
		if(existingForecast==null || !id.equalsIgnoreCase(existingForecast.getId())) {
			log.warn("No forecast found with id {}, failed to fetch saved inputs for given forecast id", id);
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
		CreatedByVO requestUser = this.userStore.getVO();
		List<String> forecastProjectUsers = new ArrayList<>();
		forecastProjectUsers.add(existingForecast.getCreatedBy().getId());
		List<CollaboratorVO> collaborators = existingForecast.getCollaborators();
		if(collaborators!=null && !collaborators.isEmpty()) {
			collaborators.forEach(n-> forecastProjectUsers.add(n.getId()));
		}
		if(forecastProjectUsers!=null && !forecastProjectUsers.isEmpty()) {
			if(!forecastProjectUsers.contains(requestUser.getId())) {
				log.warn("User not part of forecast project with id {} and name {}, Not authorized to user other project inputs",id,existingForecast.getName());
				return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
			}
		}
		List<InputFileVO> files = service.getSavedFiles(id);
		HttpStatus responseStatus = HttpStatus.OK;
		if(files== null || files.isEmpty()) {
			responseStatus = HttpStatus.NO_CONTENT;
		}
		collection.setFiles(files);
		return new ResponseEntity<>(collection, responseStatus);
	}

    
	@Override
	@ApiOperation(value = "Initialize/Create forecast project for user.", nickname = "createForecastProject", notes = "Create forecast project for user ", response = ForecastProjectResponseVO.class, tags = {
			"forecast-projects", })
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Returns message of success or failure ", response = ForecastProjectResponseVO.class),
			@ApiResponse(code = 400, message = "Bad Request", response = GenericMessage.class),
			@ApiResponse(code = 401, message = "Request does not have sufficient credentials."),
			@ApiResponse(code = 403, message = "Request is not authorized."),
			@ApiResponse(code = 405, message = "Method not allowed"),
			@ApiResponse(code = 500, message = "Internal error") })
	@RequestMapping(value = "/forecasts", produces = { "application/json" }, consumes = {
			"application/json" }, method = RequestMethod.POST)
	public ResponseEntity<ForecastProjectResponseVO> createForecastProject(
			@ApiParam(value = "Request Body that contains data required for intialize chronos project for user", required = true) @Valid @RequestBody ForecastProjectCreateRequestWrapperVO forecastRequestWrapperVO) {
		
		ForecastProjectResponseVO responseVO = new ForecastProjectResponseVO();
		ForecastProjectCreateRequestVO forecastProjectCreateVO = forecastRequestWrapperVO.getData();
		String name = forecastProjectCreateVO.getName();
		ForecastVO existingForecast = service.getByUniqueliteral("name", name);
		if(existingForecast!=null && existingForecast.getId()!=null) {
			log.error("Forecast project with this name {} already exists , failed to create forecast project", name);
			MessageDescription invalidMsg = new MessageDescription("Forecast project already exists with given name");
			GenericMessage errorMessage = new GenericMessage();
			errorMessage.setSuccess(HttpStatus.CONFLICT.name());
			errorMessage.addWarnings(invalidMsg);
			responseVO.setData(existingForecast);
			responseVO.setResponse(errorMessage);
			return new ResponseEntity<>(responseVO, HttpStatus.CONFLICT);
		}
		CreatedByVO requestUser = this.userStore.getVO();
		ForecastVO forecastVO = new ForecastVO();
		forecastVO.setApiKey(forecastProjectCreateVO.getApiKey());
		forecastVO.setBucketName(BUCKETS_PREFIX + name);
		forecastVO.setCollaborators(forecastProjectCreateVO.getCollaborators());
		forecastVO.setCreatedBy(requestUser);
		SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS+00:00");
		Date createdOn = new Date();
		try {
			createdOn = isoFormat.parse(isoFormat.format(createdOn));
		}catch(Exception e) {
			log.warn("Failed to format createdOn date to ISO format");
		}
		forecastVO.setCreatedOn(createdOn);
		forecastVO.setId(null);
		forecastVO.setName(forecastProjectCreateVO.getName());
		forecastVO.setRuns(null);
		forecastVO.setSavedInputs(null);
		try {
			ForecastVO createdVO = new ForecastVO();
			createdVO = service.createForecast(forecastVO);
			if (createdVO != null && createdVO.getId() != null) {
				GenericMessage successResponse = new GenericMessage();
				successResponse.setSuccess("SUCCESS");
				successResponse.setErrors(null);
				successResponse.setWarnings(null);
				responseVO.setData(createdVO);
				responseVO.setResponse(successResponse);
				log.info("ForecastProject {} created successfully", name);
				return new ResponseEntity<>(responseVO, HttpStatus.CREATED);
			} else {
				GenericMessage failedResponse = new GenericMessage();
				List<MessageDescription> messages = new ArrayList<>();
				MessageDescription message = new MessageDescription();
				message.setMessage("Failed to save due to internal error");
				messages.add(message);
				failedResponse.addErrors(message);
				failedResponse.setSuccess("FAILED");
				responseVO.setData(forecastVO);
				responseVO.setResponse(failedResponse);
				log.error("Forecast project {} , failed to create", name);
				return new ResponseEntity<>(responseVO, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			GenericMessage failedResponse = new GenericMessage();
			List<MessageDescription> messages = new ArrayList<>();
			MessageDescription message = new MessageDescription();
			message.setMessage("Failed to save due to internal error");
			messages.add(message);
			failedResponse.addErrors(message);
			failedResponse.setSuccess("FAILED");
			responseVO.setData(forecastVO);
			responseVO.setResponse(failedResponse);
			log.error("Exception occurred:{} while creating forecast project {} ", e.getMessage(), name);
			return new ResponseEntity<>(responseVO, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	@ApiOperation(value = "Get all forecast projects for the user.", nickname = "getAll", notes = "Get all forecasts projects for the user.", response = ForecastCollectionVO.class, tags = {
			"forecast-projects", })
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Returns message of success or failure", response = ForecastCollectionVO.class),
			@ApiResponse(code = 204, message = "Fetch complete, no content found."),
			@ApiResponse(code = 400, message = "Bad request."),
			@ApiResponse(code = 401, message = "Request does not have sufficient credentials."),
			@ApiResponse(code = 403, message = "Request is not authorized."),
			@ApiResponse(code = 405, message = "Method not allowed"),
			@ApiResponse(code = 500, message = "Internal error") })
	@RequestMapping(value = "/forecasts", produces = { "application/json" }, consumes = {
			"application/json" }, method = RequestMethod.GET)
	public ResponseEntity<ForecastCollectionVO> getAll(
			@ApiParam(value = "page number from which listing of forecasts should start. Offset. Example 2") @Valid @RequestParam(value = "offset", required = false) Integer offset,
			@ApiParam(value = "page size to limit the number of forecasts, Example 15") @Valid @RequestParam(value = "limit", required = false) Integer limit) {
		
			ForecastCollectionVO collection = new ForecastCollectionVO();
			int defaultLimit = 10;
			if (offset == null || offset < 0)
				offset = 0;
			if (limit == null || limit < 0) {
				limit = defaultLimit;
			}
			CreatedByVO requestUser = this.userStore.getVO();
			String user = requestUser.getId();
			List<ForecastVO> records = service.getAll(limit, offset, user);
			Long count = service.getCount(user);
			HttpStatus responseCode = HttpStatus.NO_CONTENT;
			if(records!=null && !records.isEmpty()) {
				collection.setRecords(records);
				collection.setTotalCount(count.intValue());
				responseCode = HttpStatus.OK;
			}
		return new ResponseEntity<>(collection, responseCode);
	}

	@Override
	@ApiOperation(value = "Get forecasts details for a given Id.", nickname = "getById", notes = "Get forecasts details for a given Id.", response = ForecastVO.class, tags = {
			"forecast-projects", })
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Returns message of success or failure", response = ForecastVO.class),
			@ApiResponse(code = 204, message = "Fetch complete, no content found."),
			@ApiResponse(code = 400, message = "Bad request."),
			@ApiResponse(code = 401, message = "Request does not have sufficient credentials."),
			@ApiResponse(code = 403, message = "Request is not authorized."),
			@ApiResponse(code = 405, message = "Method not allowed"),
			@ApiResponse(code = 500, message = "Internal error") })
	@RequestMapping(value = "/forecasts/{id}", produces = { "application/json" }, consumes = {
			"application/json" }, method = RequestMethod.GET)
	public ResponseEntity<ForecastVO> getById(
			@ApiParam(value = "forecast project ID to be fetched", required = true) @PathVariable("id") String id){
		ForecastVO existingForecast = service.getById(id);
		if(existingForecast==null || !id.equalsIgnoreCase(existingForecast.getId())) {
			log.warn("No forecast found with id {}, failed to fetch saved inputs for given forecast id", id);
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
		CreatedByVO requestUser = this.userStore.getVO();
		List<String> forecastProjectUsers = new ArrayList<>();
		forecastProjectUsers.add(existingForecast.getCreatedBy().getId());
		List<CollaboratorVO> collaborators = existingForecast.getCollaborators();
		if(collaborators!=null && !collaborators.isEmpty()) {
			collaborators.forEach(n-> forecastProjectUsers.add(n.getId()));
		}
		if(forecastProjectUsers!=null && !forecastProjectUsers.isEmpty()) {
			if(!forecastProjectUsers.contains(requestUser.getId())) {
				log.warn("User not part of forecast project with id {} and name {}, Not authorized to user other project inputs",id,existingForecast.getName());
				return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
			}else {
				return new ResponseEntity<>(existingForecast, HttpStatus.OK);
			}
		}
		return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
	}

	@Override
	@ApiOperation(value = "delete particular run", nickname = "deleteRun", notes = "delete particular run based on id", response = GenericMessage.class, tags = {
			"forecast-runs", })
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Returns message of success or failure", response = GenericMessage.class),
			@ApiResponse(code = 204, message = "Fetch complete, no content found."),
			@ApiResponse(code = 400, message = "Bad request."),
			@ApiResponse(code = 401, message = "Request does not have sufficient credentials."),
			@ApiResponse(code = 403, message = "Request is not authorized."),
			@ApiResponse(code = 405, message = "Method not allowed"),
			@ApiResponse(code = 500, message = "Internal error") })
	@RequestMapping(value = "/forecasts/{id}/runs/{rid}", produces = { "application/json" }, consumes = {
			"application/json" }, method = RequestMethod.DELETE)
	public ResponseEntity<GenericMessage> deleteRun(
			@ApiParam(value = "forecast project ID ", required = true) @PathVariable("id") String id,
			@ApiParam(value = "run id ", required = true) @PathVariable("rid") String rid) {
		return null;
	}

	@Override
	@ApiOperation(value = "Get all forecast projects for the user.", nickname = "getAllRunsForProject", notes = "Get all forecasts projects for the user.", response = ForecastRunCollectionVO.class, tags = {
			"forecast-runs", })
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Returns message of success or failure", response = ForecastRunCollectionVO.class),
			@ApiResponse(code = 204, message = "Fetch complete, no content found."),
			@ApiResponse(code = 400, message = "Bad request."),
			@ApiResponse(code = 401, message = "Request does not have sufficient credentials."),
			@ApiResponse(code = 403, message = "Request is not authorized."),
			@ApiResponse(code = 405, message = "Method not allowed"),
			@ApiResponse(code = 500, message = "Internal error") })
	@RequestMapping(value = "/forecasts/{id}/runs", produces = { "application/json" }, consumes = {
			"application/json" }, method = RequestMethod.GET)
	public ResponseEntity<ForecastRunCollectionVO> getAllRunsForProject(
			@ApiParam(value = "forecast project ID ", required = true) @PathVariable("id") String id,
			@ApiParam(value = "page number from which listing of forecasts should start. Offset. Example 2") @Valid @RequestParam(value = "offset", required = false) Integer offset,
			@ApiParam(value = "page size to limit the number of forecasts, Example 15") @Valid @RequestParam(value = "limit", required = false) Integer limit) {

		ForecastRunCollectionVO collection = new ForecastRunCollectionVO();
		int defaultLimit = 10;
		if (offset == null || offset < 0)
			offset = 0;
		if (limit == null || limit < 0) {
			limit = defaultLimit;
		}
		CreatedByVO requestUser = this.userStore.getVO();
		String user = requestUser.getId();
		ForecastVO existingForecast = service.getById(id);
		//validate user
		List<RunVO> records = service.getAllRunsForProject(limit, offset, existingForecast);
		Long count = service.getRunsCount(id);
		HttpStatus responseCode = HttpStatus.NO_CONTENT;
		if(records!=null && !records.isEmpty()) {
			collection.setRecords(records);
			collection.setTotalCount(count.intValue());
			responseCode = HttpStatus.OK;
		}
	return new ResponseEntity<>(collection, responseCode);
	}

	@Override
	@ApiOperation(value = "Get all forecast project run visualization for the user.", nickname = "getRunVisualizationData", notes = "Get all forecasts projects for the user.", response = RunVisualizationVO.class, tags = {
			"forecast-runs", })
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Returns message of success or failure", response = RunVisualizationVO.class),
			@ApiResponse(code = 204, message = "Fetch complete, no content found."),
			@ApiResponse(code = 400, message = "Bad request."),
			@ApiResponse(code = 401, message = "Request does not have sufficient credentials."),
			@ApiResponse(code = 403, message = "Request is not authorized."),
			@ApiResponse(code = 405, message = "Method not allowed"),
			@ApiResponse(code = 500, message = "Internal error") })
	@RequestMapping(value = "/forecasts/{id}/runs/{rid}", produces = { "application/json" }, consumes = {
			"application/json" }, method = RequestMethod.GET)
	public ResponseEntity<RunVisualizationVO> getRunVisualizationData(
			@ApiParam(value = "forecast project ID ", required = true) @PathVariable("id") String id,
			@ApiParam(value = "run id ", required = true) @PathVariable("rid") String rid) {
		return null;
	}

	@Override
	@ApiOperation(value = "Create new run for forecast project.", nickname = "createForecastRun", notes = "Create run for forecast project", response = ForecastRunResponseVO.class, tags={ "forecast-runs", })
    @ApiResponses(value = { 
        @ApiResponse(code = 201, message = "Returns message of success or failure ", response = ForecastRunResponseVO.class),
        @ApiResponse(code = 400, message = "Bad Request", response = GenericMessage.class),
        @ApiResponse(code = 401, message = "Request does not have sufficient credentials."),
        @ApiResponse(code = 403, message = "Request is not authorized."),
        @ApiResponse(code = 405, message = "Method not allowed"),
        @ApiResponse(code = 500, message = "Internal error") })
    @RequestMapping(value = "/forecasts/{id}/runs",
        produces = { "application/json" }, 
        consumes = { "multipart/form-data" },
        method = RequestMethod.POST)
    public ResponseEntity<ForecastRunResponseVO> createForecastRun(@ApiParam(value = "forecast project ID ",required=true) @PathVariable("id") String id,
    		@ApiParam(value = "name of the run sample. Example YYYY-MM-DD_run_topic", required=true) @RequestParam(value="runName", required=true)  String runName,
    		@ApiParam(value = "Chronos default config yml", required=true, allowableValues="Default-Settings") @RequestParam(value="configurationFile", required=true)  String configurationFile,
    		@ApiParam(value = "frequency parameter.", required=true, allowableValues="Daily, Weekly, Monthly, Yearly, No_Frequency") @RequestParam(value="frequency", required=true)  String frequency,
    		@ApiParam(value = "Any number greater than 1", required=true) @RequestParam(value="forecastHorizon", required=true)  BigDecimal forecastHorizon,
    		@ApiParam(value = "The file to upload.") @Valid @RequestPart(value="file", required=false) MultipartFile file,
    		@ApiParam(value = "path of file in minio system, if not giving file in request part") @RequestParam(value="savedInputPath", required=false)  String savedInputPath,
    		@ApiParam(value = "flag whether to save file in request part to storage bucket for further runs") @RequestParam(value="saveRequestPart", required=false)  Boolean saveRequestPart,
    		@ApiParam(value = "Comments for the run") @RequestParam(value="comment", required=false)  String comment){
		
			ForecastRunResponseVO responseVO = new ForecastRunResponseVO();
			GenericMessage responseMessage = new GenericMessage();
			ForecastVO existingForecast = service.getById(id);
			SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS+00:00");
			Date createdOn = new Date();
			try {
				createdOn = isoFormat.parse(isoFormat.format(createdOn));
			}catch(Exception e) {
				log.warn("Failed to format createdOn date to ISO format");
			}
			if(existingForecast==null || existingForecast.getId()==null) {
				log.error("Forecast project with this id {} doesnt exists , failed to create run", id);
				MessageDescription invalidMsg = new MessageDescription("Forecast project doesnt exists with given id");
				GenericMessage errorMessage = new GenericMessage();
				errorMessage.setSuccess("FAILED");
				errorMessage.addErrors(invalidMsg);
				responseVO.setData(null);
				responseVO.setResponse(errorMessage);
				return new ResponseEntity<>(responseVO, HttpStatus.NOT_FOUND);
			}
			
			CreatedByVO requestUser = this.userStore.getVO();
			List<String> forecastProjectUsers = new ArrayList<>();
			forecastProjectUsers.add(existingForecast.getCreatedBy().getId());
			List<CollaboratorVO> collaborators = existingForecast.getCollaborators();
			if(collaborators!=null && !collaborators.isEmpty()) {
				collaborators.forEach(n-> forecastProjectUsers.add(n.getId()));
			}
			if(forecastProjectUsers!=null && !forecastProjectUsers.isEmpty()) {
				if(!forecastProjectUsers.contains(requestUser.getId())) {
					log.error("User not part of forecast project with id {} and name {}, Not authorized to user other project inputs",id,existingForecast.getName());
					MessageDescription invalidMsg = new MessageDescription("User not part of given Forecast project. Cannot initate run");
					GenericMessage errorMessage = new GenericMessage();
					errorMessage.setSuccess("FAILED");
					errorMessage.addErrors(invalidMsg);
					responseVO.setData(null);
					responseVO.setResponse(errorMessage);
					return new ResponseEntity<>(responseVO, HttpStatus.UNAUTHORIZED);
				}else {
					if(file!=null) {
						String fileName = file.getOriginalFilename();
						if (!isValidAttachment(fileName)) {
							log.error("Invalid file type {} attached for project name {} and id {} ", existingForecast.getName(), id ,fileName);
							MessageDescription invalidMsg = new MessageDescription("Invalid File type attached. Supported only xlxs and csv extensions");
							GenericMessage errorMessage = new GenericMessage();
							errorMessage.setSuccess("FAILED");
							errorMessage.addErrors(invalidMsg);
							responseVO.setData(null);
							responseVO.setResponse(errorMessage);
							return new ResponseEntity<>(responseVO, HttpStatus.BAD_REQUEST);
						}else {
							List<InputFileVO> savedInputs = existingForecast.getSavedInputs();
							if(saveRequestPart) {
								if(savedInputs!=null && !savedInputs.isEmpty()) {
									List<String> fileNames = savedInputs.stream().map(InputFileVO::getName).collect(Collectors.toList());
									if(fileNames.contains(file.getOriginalFilename())) {
										log.error("File with name already exists in saved input files list. Project {} and file {}", existingForecast.getName(),fileName);
										MessageDescription invalidMsg = new MessageDescription("File with name already exists in saved input files list. Please rename and upload again");
										GenericMessage errorMessage = new GenericMessage();
										errorMessage.setSuccess("FAILED");
										errorMessage.addErrors(invalidMsg);
										responseVO.setData(null);
										responseVO.setResponse(errorMessage);
										return new ResponseEntity<>(responseVO, HttpStatus.BAD_REQUEST);
									}
								}else
									savedInputs = new ArrayList<>();
							}
							FileUploadResponseDto fileUploadResponse = service.saveFile(file, existingForecast.getBucketName());
							if(fileUploadResponse==null || (fileUploadResponse!=null && (fileUploadResponse.getErrors()!=null || !"SUCCESS".equalsIgnoreCase(fileUploadResponse.getStatus())))) {
								GenericMessage errorMessage = new GenericMessage();
								errorMessage.setSuccess("FAILED");
								errorMessage.setErrors(fileUploadResponse.getErrors());
								errorMessage.setWarnings(fileUploadResponse.getWarnings());
								responseVO.setData(null);
								responseVO.setResponse(errorMessage);
								return new ResponseEntity<>(responseVO, HttpStatus.INTERNAL_SERVER_ERROR);
							}else if("SUCCESS".equalsIgnoreCase(fileUploadResponse.getStatus())){
									if(saveRequestPart) {
										InputFileVO currentInput = new InputFileVO();
										currentInput.setName(file.getOriginalFilename());
										currentInput.setPath(existingForecast.getBucketName()+"/inputs/"+file.getOriginalFilename());
										currentInput.setId(UUID.randomUUID().toString());
										currentInput.setCreatedOn(createdOn);
										currentInput.setCreatedBy(requestUser.getId());
										savedInputs.add(currentInput);
										existingForecast.setSavedInputs(savedInputs);								
									}
								savedInputPath = existingForecast.getBucketName()+"/inputs/"+file.getOriginalFilename();
							}
						}
				}
					ForecastRunResponseVO createRunResponse = service.createJobRun(savedInputPath, saveRequestPart, runName, configurationFile,
							frequency, forecastHorizon, comment,existingForecast,requestUser.getId(),createdOn);
					if(createRunResponse!= null && "SUCCESS".equalsIgnoreCase(createRunResponse.getResponse().getSuccess())
								&& createRunResponse.getData().getRunId()!=null) {
						return new ResponseEntity<>(createRunResponse, HttpStatus.CREATED);
					}else {
						return new ResponseEntity<>(createRunResponse, HttpStatus.INTERNAL_SERVER_ERROR);
					}
						
				}
			}
			return new ResponseEntity<>(responseVO, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	
}