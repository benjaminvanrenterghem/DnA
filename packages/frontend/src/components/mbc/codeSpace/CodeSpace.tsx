import * as React from 'react';
// @ts-ignore
import Notification from '../../../assets/modules/uilab/js/src/notification';
// @ts-ignore
import ProgressIndicator from '../../../assets/modules/uilab/js/src/progress-indicator';

// @ts-ignore
import { Envs } from '../../../globals/Envs';
import { IUserInfo } from '../../../globals/types';
import { history } from '../../../router/History';
import { trackEvent } from '../../../services/utils';
import { ApiClient } from '../../../services/ApiClient';
import Modal from '../../formElements/modal/Modal';
import Styles from './CodeSpace.scss';
import FullScreenModeIcon from '../../icons/fullScreenMode/FullScreenModeIcon';

// @ts-ignore
import Tooltip from '../../../assets/modules/uilab/js/src/tooltip';
import { useState } from 'react';
import { useEffect } from 'react';
import NewCodeSpace from './newCodeSpace/NewCodeSpace';
import ProgressWithMessage from '../../../components/progressWithMessage/ProgressWithMessage';
// import { HTTP_METHOD } from '../../../globals/constants';


export interface ICodeSpaceProps {
  user: IUserInfo;
}

export interface ICodeSpaceData {
  url: string;
  running: boolean;
}

const CodeSpace = (props: ICodeSpaceProps) => {
  const [codeSpaceData, setCodeSpaceData] = useState<ICodeSpaceData>({
    url: `https://code-spaces.***REMOVED***/${props.user.id.toLocaleLowerCase()}/`,
    running: false
  });
  const [fullScreenMode, setFullScreenMode] = useState<boolean>(false);
  const [loading, setLoading] = useState<boolean>(true);
  const [showNewCodeSpaceModal, setShowNewCodeSpaceModal] = useState<boolean>(false);
  const [isApiCallTakeTime, setIsApiCallTakeTime] = useState<boolean>(false);

  useEffect(() => {
    setLoading(false);
    ApiClient.getCodeSpace().then((res: any) => {
      const codeSpaceRunning = (res.success === 'true');
      setCodeSpaceData({
        ...codeSpaceData,
        running: codeSpaceRunning
      });
      setShowNewCodeSpaceModal(!codeSpaceRunning);
    }).catch((err: Error) => {
      Notification.show("Error in validating code space - " + err.message);
    });
  }, [])

  const toggleFullScreenMode = () => {
    setFullScreenMode(!fullScreenMode);
    trackEvent(
      'DnA Code Space',
      'View Mode',
      'Code Space iframe view changed to ' + (fullScreenMode ? 'Full Screen Mode' : 'Normal Mode'),
    );
  };

  const openInNewtab = () => {
    window.open(codeSpaceData.url, '_blank');
    trackEvent('DnA Code Space', 'Code Space Open', 'Open in New Tab');
  };

  const isCodeSpaceCreationSuccess = (status: boolean, codeSpaceData: ICodeSpaceData) => {
    setShowNewCodeSpaceModal(!status);
    setCodeSpaceData(codeSpaceData);
    Tooltip.defaultSetup();
  }

  const toggleProgressMessage = (show: boolean) => {
    setIsApiCallTakeTime(show);
  }

  const onNewCodeSpaceModalCancel = () => {
    setShowNewCodeSpaceModal(false);
    history.goBack();
  }

  return (
    <div className={fullScreenMode ? Styles.codeSpaceWrapperFSmode : '' + ' ' + Styles.codeSpaceWrapper}>
        {codeSpaceData.running &&
          <React.Fragment>
            <div className={Styles.nbheader}>
              <div className={Styles.headerdetails}>
                <img src={Envs.DNA_BRAND_LOGO_URL} className={Styles.Logo} />
                <div className={Styles.nbtitle}>
                  <h2>
                    {props.user.firstName}&apos;s Code Space 
                  </h2>
                </div>
              </div>
              <div className={Styles.navigation}>
                {codeSpaceData.running && (
                  <div className={Styles.headerright}>
                    <div tooltip-data="Open New Tab" className={Styles.OpenNewTab} onClick={openInNewtab}>
                      <i className="icon mbc-icon arrow small right" />
                      <span> &nbsp; </span>
                    </div>
                    <div onClick={toggleFullScreenMode}>
                      <FullScreenModeIcon fsNeed={fullScreenMode} />
                    </div>
                  </div>
                )}
              </div>
            </div>
            <div className={Styles.codeSpaceContent}>
              {
                <div className={Styles.codeSpace}>
                  {loading ? (
                    <div className={'progress-block-wrapper ' + Styles.preloaderCutomnize}>
                      <div className="progress infinite" />
                    </div>
                  ) : (
                    codeSpaceData.running && (
                      <div className={Styles.codespaceframe}>
                        <iframe className={fullScreenMode ? Styles.fullscreen : ''} src={codeSpaceData.url} title="Code Space"/>
                      </div>
                    )
                  )}
                </div>
              }
            </div>
          </React.Fragment>
        }
        {!codeSpaceData.running ? (
          <Modal
            title={''}
            showAcceptButton={false}
            showCancelButton={false}
            modalWidth="500px"
            buttonAlignment="right"
            show={showNewCodeSpaceModal}
            content={
              <NewCodeSpace
                user={props.user}
                isCodeSpaceCreationSuccess={isCodeSpaceCreationSuccess}
                toggleProgressMessage={toggleProgressMessage}
              />
            }
            scrollableContent={true}
            onCancel={onNewCodeSpaceModalCancel}
          />
        ) : (
          ''
        )}
      {isApiCallTakeTime && <ProgressWithMessage message={'Please wait as this process can take up to a minute....'} />}
      </div>
  );
};

export default CodeSpace;
