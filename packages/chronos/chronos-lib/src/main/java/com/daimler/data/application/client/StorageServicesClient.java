package com.daimler.data.application.client;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.daimler.data.controller.exceptions.MessageDescription;
import com.daimler.data.dto.forecast.CollaboratorVO;
import com.daimler.data.dto.forecast.CreatedByVO;
import com.daimler.data.dto.storage.CollaboratorsDto;
import com.daimler.data.dto.storage.CreateBucketRequestDto;
import com.daimler.data.dto.storage.CreateBucketRequestWrapperDto;
import com.daimler.data.dto.storage.CreateBucketResponseWrapperDto;
import com.daimler.data.dto.storage.FileUploadResponseDto;
import com.daimler.data.dto.storage.PermissionsDto;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class StorageServicesClient {

	@Value("${storage.uri}")
	private String storageBaseUri;
	
	@Value("${databricks.userid}")
	private String dataBricksUser;
	
	@Value("${databricks.userauth}")
	private String dataBricksAuth;

	private static final String BUCKETS_PATH = "/api/buckets";
	private static final String UPLOADFILE_PATH = "/upload";
	private static final String INPUTS_PREFIX_PATH = "/inputs/";
	private static final String BUCKET_CLASSIFICATION = "Internal";
	private static final Boolean PII_DATE_DEFAULT = true;
	private static final Boolean TERMS_OF_USE = true;
	
	@Autowired
	HttpServletRequest httpRequest;
	
	@Autowired
	private RestTemplate restClient;
	
	public CreateBucketResponseWrapperDto createBucket(String bucketName,CreatedByVO creator, List<CollaboratorVO> collaborators) {
		CreateBucketResponseWrapperDto createBucketResponse = new CreateBucketResponseWrapperDto();
		List<MessageDescription> errors = new ArrayList<>();
		try {
				HttpHeaders headers = new HttpHeaders();
				headers.set("Accept", "application/json");
				headers.set("Authorization", dataBricksAuth);
				headers.setContentType(MediaType.APPLICATION_JSON);
				
				String uploadFileUrl = storageBaseUri + BUCKETS_PATH;
				CreateBucketRequestWrapperDto requestWrapper = new CreateBucketRequestWrapperDto();
				CreateBucketRequestDto data = new CreateBucketRequestDto();
				PermissionsDto permissions = new PermissionsDto();
				permissions.setRead(true);
				permissions.setWrite(false);
				data.setBucketName(bucketName);
				data.setClassificationType(BUCKET_CLASSIFICATION);
				data.setPiiData(PII_DATE_DEFAULT);
				data.setTermsOfUse(TERMS_OF_USE);
				if(collaborators!=null && !collaborators.isEmpty()) {
						List<CollaboratorsDto> bucketCollaborators = collaborators.stream().map
										(n -> { CollaboratorsDto collaborator = new CollaboratorsDto();
										BeanUtils.copyProperties(n,collaborator);
										collaborator.setAccesskey(n.getId());
										collaborator.setPermission(permissions);
										return collaborator;
								}).collect(Collectors.toList());
						data.setCollaborators(bucketCollaborators);
				}
				CollaboratorsDto creatorAsCollab = new CollaboratorsDto();
				BeanUtils.copyProperties(creator,creatorAsCollab);
				creatorAsCollab.setAccesskey(creator.getId());
				creatorAsCollab.setPermission(permissions);
				data.getCollaborators().add(creatorAsCollab);
				requestWrapper.setData(data);
				HttpEntity<CreateBucketRequestWrapperDto> requestEntity = new HttpEntity<>(requestWrapper,headers);
				ResponseEntity<CreateBucketResponseWrapperDto> response = restClient.exchange(uploadFileUrl, HttpMethod.POST,
						requestEntity, CreateBucketResponseWrapperDto.class);
				if (response.hasBody()) {
					createBucketResponse = response.getBody();
				}
				}catch(Exception e) {
					log.error("Failed while creating bucket {} with exception {}",  bucketName,e.getMessage());
					MessageDescription errMsg = new MessageDescription("Failed while creating bucket with exception " + e.getMessage());
					errors.add(errMsg);
					createBucketResponse.setErrors(errors);
					createBucketResponse.setStatus("FAILED");
				}
			return createBucketResponse;
	}
	
	public FileUploadResponseDto uploadFile(MultipartFile file,String bucketName) {
		FileUploadResponseDto uploadResponse = new FileUploadResponseDto();
		List<MessageDescription> errors = new ArrayList<>();
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.set("Accept", "application/json");
			headers.set("Authorization", dataBricksAuth);
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			LinkedMultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();
	
			ByteArrayResource fileAsResource = new ByteArrayResource(file.getBytes()){
			    @Override
			    public String getFilename(){
			        return file.getOriginalFilename();                                          
			    }
			};
			String uploadFileUrl = storageBaseUri + BUCKETS_PATH + "/" + bucketName + UPLOADFILE_PATH;
			HttpEntity<ByteArrayResource> attachmentPart = new HttpEntity<>(fileAsResource);
			multipartRequest.set("file",attachmentPart);
			multipartRequest.set("prefix",INPUTS_PREFIX_PATH);
			HttpEntity<LinkedMultiValueMap<String,Object>> requestEntity = new HttpEntity<>(multipartRequest,headers);
			ResponseEntity<FileUploadResponseDto> response = restClient.exchange(uploadFileUrl, HttpMethod.POST,
					requestEntity, FileUploadResponseDto.class);
			if (response.hasBody()) {
				uploadResponse = response.getBody();
			}
			}catch(Exception e) {
				log.error("Failed while uploading file {} to minio bucket {} with exception {}", file.getOriginalFilename(), bucketName,e.getMessage());
				MessageDescription errMsg = new MessageDescription("Failed while uploading file with exception " + e.getMessage());
				errors.add(errMsg);
				uploadResponse.setErrors(errors);
				uploadResponse.setStatus("FAILED");
			}
		return uploadResponse;
	}
	
	
	
	
}