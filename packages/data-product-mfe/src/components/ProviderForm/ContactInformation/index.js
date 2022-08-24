import classNames from 'classnames';
import React, { useEffect, useState } from 'react';
import Styles from './styles.scss';

// components from container app
import SelectBox from 'dna-container/SelectBox';
import InfoModal from 'dna-container/InfoModal';
import Tags from 'dna-container/Tags';

import { useFormContext, Controller } from 'react-hook-form';
import { hostServer } from '../../../server/api';

import ProgressIndicator from '../../../common/modules/uilab/js/src/progress-indicator';
import Notification from '../../../common/modules/uilab/js/src/notification';

import { useSelector } from 'react-redux';
import { dataProductsApi } from '../../../apis/dataproducts.api';

const ContactInformation = ({ onSave, divisions, setSubDivisions, subDivisions }) => {
  const {
    register,
    formState: { errors, isSubmitting, dirtyFields },
    watch,
    handleSubmit,
    trigger,
    reset,
    setValue,
    control,
  } = useFormContext();
  const [showInfoModal, setShowInfoModal] = useState(false);
  const provideDataProducts = useSelector((state) => state.provideDataProducts);

  const { division, complianceOfficer: selectedcomplianceOfficer } = watch();

  const [complianceOfficerList, setComplianceOfficerList] = useState({
    records: [],
    totalCount: 0,
  });
  const [complianceOfficer, setComplianceOfficer] = useState([]);

  useEffect(() => {
    const id = watch('division');
    if (id > '0') {
      ProgressIndicator.show();
      hostServer.get('/subdivisions/' + id).then((res) => {
        setSubDivisions(res?.data || []);
        if (!dirtyFields.division && !dirtyFields.subDivision) {
          setValue('subDivision', provideDataProducts.selectedDataProduct.subDivision);
        } else {
          setValue('subDivision', '0');
        }
        SelectBox.defaultSetup();
        ProgressIndicator.hide();
      });
    } else {
      setSubDivisions([]);
      if (dirtyFields.division) {
        setValue('subDivision', '0');
        SelectBox.defaultSetup();
      }
    }
    //eslint-disable-next-line react-hooks/exhaustive-deps
  }, [division]);

  useEffect(() => {
    SelectBox.defaultSetup();
    reset(watch());
    //eslint-disable-next-line
  }, []);

  useEffect(() => {
    // if division is set to "Choose", reset sub divison dropdown to empty list
    division === '0' && SelectBox.defaultSetup();
  }, [division, subDivisions.length]);

  useEffect(() => {
    ProgressIndicator.show();
    dataProductsApi
      .getDataComplianceList(0, 0, 'entityId', 'asc')
      .then((res) => {
        res.data?.records?.map((item) => {
          item['name'] = item.localComplianceOfficer.toString();
          return item;
        });
        setComplianceOfficerList(res.data);
        ProgressIndicator.hide();
      })
      .catch((e) => {
        ProgressIndicator.hide();
        Notification.show(
          e.response?.data?.errors?.[0]?.message || 'Error while fethcing data compliance officers list.',
          'alert',
        );
      });
    //eslint-disable-next-line
  }, []);

  useEffect(() => {
    if (selectedcomplianceOfficer?.length) {
      setComplianceOfficer(selectedcomplianceOfficer);
    }
  }, [selectedcomplianceOfficer]);

  return (
    <>
      <div className={Styles.wrapper}>
        <div className={classNames(Styles.firstPanel, 'descriptionSection')}>
          <div>
            <h3>Contact Information</h3>
            <div className={Styles.infoIcon}>
              <i className={'icon mbc-icon info'} onClick={() => setShowInfoModal(true)} />
            </div>
          </div>
          <div className={Styles.formWrapper}>
            <div className={Styles.flexLayout}>
              <div className={classNames('input-field-group include-error', errors.productName ? 'error' : '')}>
                <label id="productNameLabel" htmlFor="productNameInput" className="input-label">
                  Data Product Name <sup>*</sup>
                </label>
                <input
                  {...register('productName', { required: '*Missing entry' })}
                  type="text"
                  className="input-field"
                  id="productNameInput"
                  maxLength={200}
                  placeholder="Type here"
                  autoComplete="off"
                />
                <span className={classNames('error-message')}>{errors.productName?.message}</span>
              </div>
              <div className={classNames('input-field-group include-error', errors.dateOfDataTransfer ? 'error' : '')}>
                <label id="dateOfDataTransferLabel" htmlFor="dateOfDataTransferInput" className="input-label">
                  Date of Data Transfer <sup>*</sup>
                </label>
                <input
                  {...register('dateOfDataTransfer', {
                    required: '*Missing entry',
                  })}
                  type="text"
                  className="input-field"
                  id="dateOfDataTransferInput"
                  maxLength={200}
                  placeholder="Type here"
                  autoComplete="off"
                />
                <span className={classNames('error-message')}>{errors.dateOfDataTransfer?.message}</span>
              </div>
            </div>
            <div className={Styles.flexLayout}>
              <div className={classNames('input-field-group include-error', errors.name ? 'error' : '')}>
                <label id="nameLabel" htmlFor="nameInput" className="input-label">
                  Your Name <sup>*</sup>
                </label>
                <input
                  {...register('name', { required: '*Missing entry' })}
                  type="text"
                  className="input-field"
                  id="nameInput"
                  maxLength={200}
                  placeholder="Type here"
                  autoComplete="off"
                />
                <span className={classNames('error-message')}>{errors.name?.message}</span>
              </div>
              <div className={classNames(Styles.flexLayout)}>
                <div className={Styles.divisionContainer}>
                  <div
                    className={classNames(
                      `input-field-group include-error ${errors?.division?.message ? 'error' : ''}`,
                    )}
                  >
                    <label id="divisionLabel" htmlFor="divisionField" className="input-label">
                      Division <sup>*</sup>
                    </label>
                    <div className="custom-select" onBlur={() => trigger('division')}>
                      <select
                        id="divisionField"
                        required={true}
                        required-error={'*Missing entry'}
                        {...register('division', {
                          validate: (value) => value !== '0' || '*Missing entry',
                        })}
                      >
                        <option id="divisionOption" value={0}>
                          Choose
                        </option>
                        {divisions?.map((obj) => (
                          <option id={obj.name + obj.id} key={obj.id} value={obj.id}>
                            {obj.name}
                          </option>
                        ))}
                      </select>
                    </div>
                    <span className={classNames('error-message')}>{errors?.division?.message}</span>
                  </div>
                </div>
                <div className={Styles.subDivisionContainer}>
                  <div className={classNames('input-field-group')}>
                    <label id="subDivisionLabel" htmlFor="subDivisionField" className="input-label">
                      Sub Division
                    </label>
                    <div className="custom-select">
                      <select id="subDivisionField" {...register('subDivision')}>
                        {subDivisions?.some((item) => item.id === '0' && item.name === 'None') ? (
                          <option id="subDivisionDefault" value={0}>
                            None
                          </option>
                        ) : (
                          <>
                            <option id="subDivisionDefault" value={0}>
                              Choose
                            </option>
                            {subDivisions?.map((obj) => (
                              <option id={obj.name + obj.id} key={obj.id} value={obj.id}>
                                {obj.name}
                              </option>
                            ))}
                          </>
                        )}
                      </select>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div className={Styles.flexLayout}>
              <div className={classNames('input-field-group include-error', errors.department ? 'error' : '')}>
                <label id="departmentLabel" htmlFor="departmentInput" className="input-label">
                  Department <sup>*</sup>
                </label>
                <input
                  {...register('department', { required: '*Missing entry' })}
                  type="text"
                  className="input-field"
                  id="departmentInput"
                  maxLength={200}
                  placeholder="Type here"
                  autoComplete="off"
                />
                <span className={classNames('error-message')}>{errors.department?.message}</span>
              </div>
            </div>
            <div className={Styles.flexLayout}>
              <div className={classNames('input-field-group include-error', errors.planningIT ? 'error' : '')}>
                <label id="planningITLabel" htmlFor="planningITInput" className="input-label">
                  planningIT App-ID <sup>*</sup>
                </label>
                <input
                  {...register('planningIT', { required: '*Missing entry' })}
                  type="text"
                  className="input-field"
                  id="planningITInput"
                  maxLength={200}
                  placeholder="Type here"
                  autoComplete="off"
                />
                <span className={classNames('error-message')}>{errors.planningIT?.message}</span>
              </div>
              <div className={classNames('input-field-group include-error', errors.complianceOfficer ? 'error' : '')}>
                <Controller
                  control={control}
                  name="complianceOfficer"
                  rules={{ required: '*Missing entry' }}
                  render={({ field }) => (
                    <Tags
                      title={'Corresponding Compliance Officer / Responsible (LCO/LCR)'}
                      max={1}
                      chips={complianceOfficer}
                      tags={complianceOfficerList.records}
                      setTags={(selectedTags) => {
                        setComplianceOfficer(selectedTags);
                        field.onChange(selectedTags);
                      }}
                      suggestionRender={(item) => (
                        <div className={Styles.optionContainer}>
                          <span className={Styles.optionText}>Entity ID: {item?.entityId}</span>
                          <span className={Styles.optionText}>Entiry Name: {item?.entityName}</span>
                          <span className={Styles.optionText}>LCO: {item?.name}</span>
                        </div>
                      )}
                      isMandatory={true}
                      showMissingEntryError={errors.complianceOfficer?.message}
                      disableOnBlurAdd={true}
                      suggestionPopupHeight={120}
                    />
                  )}
                />
              </div>
            </div>
          </div>
          <div className="btnContainer">
            <button
              className="btn btn-primary"
              type="submit"
              disabled={isSubmitting}
              onClick={handleSubmit((values) => {
                reset(values, {
                  keepDirty: false,
                });
                onSave(values);
              })}
            >
              Save & Next
            </button>
          </div>
        </div>
      </div>
      {showInfoModal && (
        <InfoModal
          title="Info Modal"
          show={showInfoModal}
          hiddenTitle={true}
          content={<div>Sample Info Modal</div>}
          onCancel={() => setShowInfoModal(false)}
        />
      )}
    </>
  );
};

export default ContactInformation;