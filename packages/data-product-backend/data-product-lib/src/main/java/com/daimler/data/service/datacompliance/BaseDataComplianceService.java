/* LICENSE START
 * 
 * MIT License
 * 
 * Copyright (c) 2019 Daimler TSS GmbH
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * LICENSE END 
 */

package com.daimler.data.service.datacompliance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import com.daimler.data.application.auth.UserStore;
import com.daimler.data.assembler.DataComplianceAssembler;
import com.daimler.data.controller.exceptions.GenericMessage;
import com.daimler.data.controller.exceptions.MessageDescription;
import com.daimler.data.db.entities.DataComplianceNsql;
import com.daimler.data.db.repo.datacompliance.DataComplianceCustomRepository;
import com.daimler.data.db.repo.datacompliance.DataComplianceRepository;
import com.daimler.data.dto.datacompliance.DataComplianceResponseVO;
import com.daimler.data.dto.datacompliance.DataComplianceVO;
import com.daimler.data.service.common.BaseCommonService;

@Service
@SuppressWarnings(value = "unused")
public class BaseDataComplianceService extends BaseCommonService<DataComplianceVO, DataComplianceNsql, String>
		implements DataComplianceService {

	private static Logger LOGGER = LoggerFactory.getLogger(BaseDataComplianceService.class);

	@Autowired
	private UserStore userStore;

	@Autowired
	private DataComplianceAssembler dataComplianceAssembler;

	@Autowired
	private DataComplianceCustomRepository dataComplianceCustomRepository;

	@Autowired
	private DataComplianceRepository dataComplianceRepository;

	public BaseDataComplianceService() {
		super();
	}

	@Override
	@Transactional
	public DataComplianceVO create(DataComplianceVO vo) {
		return super.create(vo);
	}

	@Override
	public List<DataComplianceVO> getAllWithFilters(String entityId, String entityName,
			List<String> localComplianceOfficer, List<String> localComplianceResponsible,
			List<String> dataProtectionCoordinator, List<String> localComplianceSpecialist, int offset, int limit,
			String sortBy, String sortOrder) {
		List<DataComplianceNsql> entities = dataComplianceCustomRepository.getAllWithFiltersUsingNativeQuery(entityId,
				entityName, localComplianceOfficer, localComplianceResponsible, dataProtectionCoordinator,
				localComplianceSpecialist, offset, limit, sortBy, sortOrder);
		if (!ObjectUtils.isEmpty(entities))
			return entities.stream().map(n -> dataComplianceAssembler.toVo(n)).collect(Collectors.toList());
		else
			return new ArrayList<>();
	}

	@Override
	public Long getCount(String entityId, String entityName, List<String> localComplianceOfficer,
			List<String> localComplianceResponsible, List<String> dataProtectionCoordinator,
			List<String> localComplianceSpecialist) {
		return dataComplianceCustomRepository.getCountUsingNativeQuery(entityId, entityName, localComplianceOfficer,
				localComplianceResponsible, dataProtectionCoordinator, localComplianceSpecialist);
	}

	@Override
	@Transactional
	public ResponseEntity<DataComplianceResponseVO> createDataCompliance(DataComplianceVO requestDataComplianceVO) {
		DataComplianceResponseVO dataComplianceResponseVO = new DataComplianceResponseVO();
		try {
			String uniqueEntityId = requestDataComplianceVO.getEntityId();
			String uniqueEntityName = requestDataComplianceVO.getEntityName();
			DataComplianceVO existingDataComplianceVO = super.getByUniqueliteral("entityId", uniqueEntityId);
			if (existingDataComplianceVO != null && existingDataComplianceVO.getEntityId() != null) {
				dataComplianceResponseVO.setData(existingDataComplianceVO);
				List<MessageDescription> messages = new ArrayList<>();
				MessageDescription message = new MessageDescription();
				message.setMessage("Record already exists for given entityId.");
				messages.add(message);
				dataComplianceResponseVO.setErrors(messages);
				LOGGER.info("EntityId {} already exists, returning as CONFLICT", uniqueEntityId);
				return new ResponseEntity<>(dataComplianceResponseVO, HttpStatus.CONFLICT);
			}
			requestDataComplianceVO.setCreatedBy(this.userStore.getVO());
			requestDataComplianceVO.setCreatedDate(new Date());
			requestDataComplianceVO.setId(null);

			DataComplianceVO dataComplianceVO = this.create(requestDataComplianceVO);
			if (dataComplianceVO != null && dataComplianceVO.getId() != null) {
				dataComplianceResponseVO.setData(dataComplianceVO);
				LOGGER.info("Data Compliance entry {} created successfully", uniqueEntityId);
				return new ResponseEntity<>(dataComplianceResponseVO, HttpStatus.CREATED);
			} else {
				List<MessageDescription> messages = new ArrayList<>();
				MessageDescription message = new MessageDescription();
				message.setMessage("Failed to save due to internal error");
				messages.add(message);
				dataComplianceResponseVO.setData(requestDataComplianceVO);
				dataComplianceResponseVO.setErrors(messages);
				LOGGER.error("Failed to create record with entityId {}", uniqueEntityId);
				return new ResponseEntity<>(dataComplianceResponseVO, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			LOGGER.error("Exception occurred:{} while creating record with entityId {} ", e.getMessage(),
					requestDataComplianceVO.getEntityId());
			List<MessageDescription> messages = new ArrayList<>();
			MessageDescription message = new MessageDescription();
			message.setMessage(e.getMessage());
			messages.add(message);
			dataComplianceResponseVO.setData(requestDataComplianceVO);
			dataComplianceResponseVO.setErrors(messages);
			return new ResponseEntity<>(dataComplianceResponseVO, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	@Transactional
	public ResponseEntity<DataComplianceResponseVO> updateDataCompliance(DataComplianceVO requestDataComplianceVO) {
		DataComplianceResponseVO response = new DataComplianceResponseVO();
		try {
			String id = requestDataComplianceVO.getId();
			DataComplianceVO existingVO = super.getById(id);
			DataComplianceVO mergedDataComplianceVO = null;

			if (existingVO != null && existingVO.getId() != null) {
				String uniqueEntityId = requestDataComplianceVO.getEntityId();
				DataComplianceVO existingEntityIdVO = super.getByUniqueliteral("entityId", uniqueEntityId);
				if (existingEntityIdVO != null && existingEntityIdVO.getEntityId() != null
						&& !existingEntityIdVO.getId().equals(id)) {
					response.setData(existingEntityIdVO);
					List<MessageDescription> messages = new ArrayList<>();
					MessageDescription message = new MessageDescription();
					message.setMessage("DataCompliance already exists.");
					messages.add(message);
					response.setErrors(messages);
					LOGGER.info("DataCompliance {} already exists, returning as CONFLICT", uniqueEntityId);
					return new ResponseEntity<>(response, HttpStatus.CONFLICT);
				}
				requestDataComplianceVO.setLastModifiedDate(new Date());
				requestDataComplianceVO.setModifiedBy(this.userStore.getVO());
				mergedDataComplianceVO = super.create(requestDataComplianceVO);
				if (mergedDataComplianceVO != null && mergedDataComplianceVO.getId() != null) {
					response.setData(mergedDataComplianceVO);
					response.setErrors(null);
					LOGGER.info("DataCompliance with id {} updated successfully", id);
					return new ResponseEntity<>(response, HttpStatus.OK);
				} else {
					List<MessageDescription> messages = new ArrayList<>();
					MessageDescription message = new MessageDescription();
					message.setMessage("Failed to update due to internal error");
					messages.add(message);
					response.setData(requestDataComplianceVO);
					response.setErrors(messages);
					LOGGER.info("DataCompliance with id {} cannot be edited. Failed with unknown internal error", id);
					return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
				}

			} else {
				List<MessageDescription> notFoundmessages = new ArrayList<>();
				MessageDescription notFoundmessage = new MessageDescription();
				notFoundmessage.setMessage("No DataCompliance found for given id. Update cannot happen");
				notFoundmessages.add(notFoundmessage);
				response.setErrors(notFoundmessages);
				LOGGER.info("No DataCompliance found for given id {} , update cannot happen.", id);
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			}

		} catch (Exception e) {
			LOGGER.error("DataCompliance with id {} cannot be edited. Failed due to internal error {} ",
					requestDataComplianceVO.getId(), e.getMessage());
			List<MessageDescription> messages = new ArrayList<>();
			MessageDescription message = new MessageDescription();
			message.setMessage("Failed to update due to internal error. " + e.getMessage());
			messages.add(message);
			response.setData(requestDataComplianceVO);
			response.setErrors(messages);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	private boolean canProceedToEdit() {
		boolean canProceed = false;
		boolean hasAdminAccess = this.userStore.getUserInfo().hasAdminAccess();
		if (hasAdminAccess) {
			canProceed = true;
		}
		return canProceed;
	}

	@Override
	@Transactional
	public ResponseEntity<GenericMessage> deleteDataCompliance(String id) {
		try {
			DataComplianceVO dataCompliance = this.getById(id);
//			if (canProceedToEdit()) {
			this.deleteById(id);
			GenericMessage successMsg = new GenericMessage();
			successMsg.setSuccess("success");
			LOGGER.info("DataCompliance entry with id {} deleted successfully", id);
			return new ResponseEntity<>(successMsg, HttpStatus.OK);
//			} else {
//				MessageDescription notAuthorizedMsg = new MessageDescription();
//				notAuthorizedMsg.setMessage("Not authorized to delete dataCompliance entry.");
//				GenericMessage errorMessage = new GenericMessage();
//				errorMessage.addErrors(notAuthorizedMsg);
//				LOGGER.debug("DataCompliance enrty with id {} cannot be deleted. User not authorized", id);
//				return new ResponseEntity<>(errorMessage, HttpStatus.FORBIDDEN);
//			}
		} catch (EntityNotFoundException e) {
			MessageDescription invalidMsg = new MessageDescription("No dataCompliance entry with the given id");
			GenericMessage errorMessage = new GenericMessage();
			errorMessage.addErrors(invalidMsg);
			LOGGER.error("No dataCompliance entry with the given id {} , could not delete.", id);
			return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			MessageDescription exceptionMsg = new MessageDescription("Failed to delete due to internal error.");
			GenericMessage errorMessage = new GenericMessage();
			errorMessage.addErrors(exceptionMsg);
			LOGGER.error("Failed to delete dataCompliance with id {} , due to internal error.", id);
			return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}