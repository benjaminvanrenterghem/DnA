import * as React from 'react';
// import SvgIcon from '../svgIcon/SvgIcon';

export interface IIconFooterLogoProps {
  className?: string;
}

export const IconFooterLogo = (props: IIconFooterLogoProps): JSX.Element => {
  return (
    <svg width="105px" height="39px" viewBox="0 0 105 39" version="1.1">
      <title>DA_Logo</title>
      <defs>
        <polygon id="path-1" points="0.005888 0.29568 6.400768 0.29568 6.400768 7.995264 0.005888 7.995264" />
        <polygon id="path-3" points="0.428416 0.29568 6.823296 0.29568 6.823296 7.995264 0.428416 7.995264" />
      </defs>
      <g id="Portal-" stroke="none" strokeWidth="1" fill="none" fillRule="evenodd">
        <g id="Introduction" transform="translate(-1156.000000, -1949.000000)">
          <g id="Footer" transform="translate(297.000000, 1949.000000)">
            <g id="DA_Logo" transform="translate(859.000000, 0.000000)">
              <path
                d="M43.81344,7.31616 L44.43232,7.31616 C44.89312,7.31616 45.25216,7.29632 45.50688,7.25472 C46.08864,7.1536 46.52448,6.86112 46.81696,6.37728 C47.12992,5.86336 47.2848,5.2336 47.2848,4.48736 C47.2848,3.71872 47.112,3.08128 46.76704,2.57504 C46.456,2.1136 46.01376,1.83584 45.43968,1.7424 C45.18112,1.70144 44.74976,1.67968 44.14624,1.67968 L43.81344,1.67968 L43.81344,7.31616 Z M42.62688,0.66272 L44.18464,0.66272 C44.80864,0.66272 45.31872,0.69216 45.71616,0.75232 C46.536,0.87968 47.19456,1.2624 47.68992,1.89984 C48.23712,2.61216 48.51104,3.47616 48.51104,4.49248 C48.51104,5.54336 48.23072,6.42336 47.672,7.1376 C47.16576,7.78848 46.47584,8.16736 45.60224,8.27232 C45.24576,8.31712 44.71968,8.34144 44.02208,8.34144 L42.62688,8.34144 L42.62688,0.66272 Z"
                id="Fill-1"
                fill="#D9DFE4"
              />
              <g id="Group-5" transform="translate(49.920000, 0.344576)">
                <mask id="mask-2" fill="white">
                  <use xlinkHref="#path-1" />
                </mask>
                <g id="Clip-4" />
                <path
                  d="M4.285568,5.171584 L3.200768,1.718784 L2.115968,5.171584 L4.285568,5.171584 Z M2.622208,0.295424 L3.774208,0.295424 L6.400768,7.995264 L5.169408,7.995264 L4.600448,6.196864 L1.805568,6.196864 L1.225728,7.995264 L0.005888,7.995264 L2.622208,0.295424 Z"
                  id="Fill-3"
                  fill="#D9DFE4"
                  mask="url(#mask-2)"
                />
              </g>
              <polygon
                id="Fill-6"
                fill="#D9DFE4"
                points="57.129344 0.6624 62.342784 0.6624 62.342784 1.68128 60.324224 1.68128 60.324224 8.33984 59.138304 8.33984 59.138304 1.68128 57.129344 1.68128"
              />
              <g id="Group-10" transform="translate(62.720000, 0.344576)">
                <mask id="mask-4" fill="white">
                  <use xlinkHref="#path-3" />
                </mask>
                <g id="Clip-9" />
                <path
                  d="M4.708096,5.171584 L3.623296,1.718784 L2.536576,5.171584 L4.708096,5.171584 Z M3.042816,0.295424 L4.196736,0.295424 L6.823296,7.995264 L5.591936,7.995264 L5.022976,6.196864 L2.228096,6.196864 L1.648256,7.995264 L0.428416,7.995264 L3.042816,0.295424 Z"
                  id="Fill-8"
                  fill="#D9DFE4"
                  mask="url(#mask-4)"
                />
              </g>
              <path
                d="M75.298112,3.513664 C75.842112,3.078464 76.112832,2.665024 76.112832,2.271424 C76.112832,2.064704 76.036672,1.903424 75.883072,1.787584 C75.755712,1.686464 75.599552,1.634624 75.415872,1.634624 C75.216832,1.634624 75.046592,1.693504 74.904512,1.809984 C74.750912,1.933504 74.672832,2.092224 74.672832,2.287424 C74.672832,2.506304 74.744512,2.727104 74.886592,2.951104 C74.958272,3.063744 75.095872,3.252544 75.298112,3.513664 L75.298112,3.513664 Z M75.089472,4.858944 C74.849472,5.046464 74.668352,5.203904 74.544192,5.331264 C74.225472,5.660864 74.065472,6.002624 74.065472,6.354624 C74.065472,6.677184 74.185792,6.948544 74.425792,7.169984 C74.680512,7.402304 75.024192,7.520064 75.455552,7.520064 C75.954112,7.520064 76.366912,7.342144 76.693312,6.991424 L75.089472,4.858944 Z M77.570752,4.645184 L78.482112,4.645184 C78.484672,4.787264 78.486592,4.901184 78.486592,4.988864 C78.486592,5.613504 78.393152,6.136384 78.205632,6.551104 C78.137792,6.695104 78.044352,6.849984 77.924672,7.018944 L78.909632,8.339904 L77.694272,8.339904 L77.283392,7.733824 C77.058112,7.951424 76.836672,8.108864 76.619072,8.204864 C76.285632,8.362304 75.890752,8.441024 75.433152,8.441024 C74.769472,8.441024 74.219072,8.273344 73.784512,7.934784 C73.259072,7.527104 72.996672,6.978624 72.996672,6.293824 C72.996672,5.849664 73.124672,5.450944 73.379392,5.095104 C73.593152,4.787264 73.952832,4.431424 74.459072,4.026304 C74.212032,3.703744 74.035392,3.463744 73.930432,3.306304 C73.728192,2.994624 73.627072,2.671424 73.627072,2.332224 C73.627072,1.863744 73.803072,1.481024 74.155712,1.184704 C74.485312,0.912704 74.904512,0.775104 75.410752,0.775104 C75.886912,0.775104 76.274112,0.899904 76.574272,1.152704 C76.911552,1.441344 77.080512,1.825984 77.080512,2.309824 C77.080512,2.776384 76.919232,3.194944 76.596672,3.569984 C76.424512,3.768384 76.191552,3.985984 75.899072,4.222144 L77.328192,6.168384 C77.414592,5.996224 77.474112,5.831104 77.508032,5.673664 C77.549632,5.463744 77.570752,5.188544 77.570752,4.847424 L77.570752,4.645184 Z"
                id="Fill-11"
                fill="#D9DFE4"
              />
              <path
                d="M46.125888,21.517056 L45.041088,18.063616 L43.954368,21.517056 L46.125888,21.517056 Z M44.460608,16.639616 L45.613248,16.639616 L48.241088,24.340736 L47.008448,24.340736 L46.440768,22.541056 L43.644608,22.541056 L43.066048,24.340736 L41.846208,24.340736 L44.460608,16.639616 Z"
                id="Fill-13"
                fill="#D9DFE4"
              />
              <polygon
                id="Fill-15"
                fill="#D9DFE4"
                points="50.633408 16.6624 51.909568 16.6624 55.295808 22.52928 55.295808 16.6624 56.420288 16.6624 56.420288 24.34112 55.194048 24.34112 51.763648 18.48512 51.763648 24.34112 50.633408 24.34112"
              />
              <path
                d="M63.014336,21.517056 L61.929536,18.063616 L60.842816,21.517056 L63.014336,21.517056 Z M61.349056,16.639616 L62.502976,16.639616 L65.129536,24.340736 L63.898176,24.340736 L63.329216,22.541056 L60.534336,22.541056 L59.954496,24.340736 L58.734656,24.340736 L61.349056,16.639616 Z"
                id="Fill-17"
                fill="#D9DFE4"
              />
              <polygon
                id="Fill-19"
                fill="#D9DFE4"
                points="67.521856 16.6624 68.742976 16.6624 68.742976 23.31584 72.055616 23.31584 72.055616 24.34112 67.521856 24.34112"
              />
              <polygon
                id="Fill-21"
                fill="#D9DFE4"
                points="72.035584 16.6624 73.379584 16.6624 75.095424 19.96992 76.788224 16.6624 78.115584 16.6624 75.669504 21.22432 75.669504 24.34112 74.488064 24.34112 74.488064 21.22432"
              />
              <polygon
                id="Fill-23"
                fill="#D9DFE4"
                points="79.005568 16.6624 84.220288 16.6624 84.220288 17.68128 82.200448 17.68128 82.200448 24.34112 81.014528 24.34112 81.014528 17.68128 79.005568 17.68128"
              />
              <polygon
                id="Fill-25"
                fill="#D9DFE4"
                points="86.61312 24.340736 87.81056 24.340736 87.81056 16.662016 86.61312 16.662016"
              />
              <path
                d="M96.608064,18.986112 L95.556544,18.986112 C95.369024,18.033792 94.840384,17.557632 93.970624,17.557632 C93.363264,17.557632 92.880704,17.807232 92.524224,18.309632 C92.138304,18.858752 91.945664,19.590912 91.945664,20.509312 C91.945664,21.436032 92.145344,22.174592 92.546624,22.725632 C92.895424,23.205632 93.377984,23.445632 93.993024,23.445632 C94.975424,23.445632 95.524544,22.830592 95.640384,21.600512 L96.720704,21.600512 C96.679104,22.291072 96.531904,22.852992 96.276544,23.288192 C95.811904,24.083072 95.063104,24.480512 94.032064,24.480512 C93.105344,24.480512 92.344384,24.173312 91.747904,23.558272 C91.047104,22.838272 90.697024,21.825792 90.697024,20.520832 C90.697024,19.107072 91.088064,18.039552 91.872064,17.319552 C92.453184,16.787072 93.156544,16.520832 93.981504,16.520832 C94.499264,16.520832 94.964544,16.633472 95.376704,16.860032 C96.104384,17.253632 96.514624,17.960832 96.608064,18.986112"
                id="Fill-27"
                fill="#D9DFE4"
              />
              <path
                d="M103.948096,18.788608 L102.800576,18.788608 C102.774336,18.462208 102.677056,18.199808 102.508096,18.001408 C102.260416,17.705088 101.904576,17.555968 101.439296,17.555968 C101.108416,17.555968 100.825536,17.639808 100.589376,17.804928 C100.319296,17.992448 100.184256,18.241408 100.184256,18.552448 C100.184256,18.833408 100.326976,19.061248 100.611776,19.232128 C100.784576,19.337088 101.221696,19.538688 101.923136,19.835008 C102.519616,20.085888 102.925376,20.274688 103.142976,20.402048 C103.829056,20.811008 104.173376,21.409408 104.173376,22.197248 C104.173376,22.950528 103.887936,23.541888 103.318336,23.969408 C102.860736,24.310528 102.265536,24.480768 101.534656,24.480768 C100.458176,24.480768 99.678016,24.131968 99.194176,23.434368 C98.920896,23.040768 98.769216,22.513408 98.739136,21.854848 L99.891776,21.854848 C99.933376,22.334848 100.064576,22.697088 100.285376,22.946048 C100.581696,23.279488 101.005376,23.445888 101.557056,23.445888 C101.961536,23.445888 102.298176,23.347328 102.564416,23.148288 C102.822976,22.956928 102.951616,22.706048 102.951616,22.394368 C102.951616,22.063488 102.785856,21.784448 102.451776,21.555968 C102.271936,21.431808 101.849536,21.229568 101.185856,20.948608 C100.405696,20.614528 99.880896,20.322048 99.610816,20.071168 C99.198016,19.692288 98.991936,19.222528 98.991936,18.659968 C98.991936,17.943808 99.275456,17.389568 99.841856,16.999808 C100.299456,16.681088 100.841536,16.522368 101.473216,16.522368 C102.020416,16.522368 102.500416,16.644608 102.913216,16.893568 C103.385536,17.175168 103.694656,17.570048 103.840576,18.080128 C103.896896,18.267648 103.933376,18.503808 103.948096,18.788608"
                id="Fill-28"
                fill="#D9DFE4"
              />
              <text
                id="MBC-"
                fontFamily="CorporateS-Demi, CorpoSDem"
                fontSize="8.106432"
                fontWeight="normal"
                letterSpacing="0.0599467874"
                fill="#00ADEF"
              >
                <tspan x="41.780032" y="35.6683991">
                  M
                </tspan>
                <tspan x="48.2483349" y="35.6683991" letterSpacing="0.0639999986">
                  BC
                </tspan>
                <tspan x="57.8681435" y="35.6683991" />
              </text>
              <text
                id="I-FM"
                fontFamily="CorporateS-Demi, CorpoSDem"
                fontSize="8.106432"
                fontWeight="normal"
                letterSpacing="0.0599467874"
                fill="#D9DFE4"
              >
                <tspan x="62.945926" y="35.6683991">
                  I
                </tspan>
                <tspan x="65.1828931" y="35.6683991" letterSpacing="0.0639999986">
                  {' '}
                  FM
                </tspan>
              </text>
              <polyline
                id="Stroke-29"
                stroke="#D9DFE4"
                strokeWidth="0.64"
                points="1.681856 27.944256 16.465216 19.394496 16.465216 2.321856"
              />
              <polyline
                id="Stroke-30"
                stroke="#D9DFE4"
                strokeWidth="0.64"
                points="31.248064 27.944256 16.464704 19.394496 16.464704 2.557376"
              />
              <path
                d="M32.929984,10.871168 C32.929984,11.799808 32.176704,12.553088 31.248064,12.553088 C30.319424,12.553088 29.566144,11.799808 29.566144,10.871168 C29.566144,9.941888 30.319424,9.189248 31.248064,9.189248 C32.176704,9.189248 32.929984,9.941888 32.929984,10.871168"
                id="Fill-31"
                fill="#D9DFE4"
              />
              <path
                d="M18.146816,19.39424 C18.146816,20.32352 17.393536,21.07616 16.464896,21.07616 C15.536256,21.07616 14.782976,20.32352 14.782976,19.39424 C14.782976,18.4656 15.536256,17.71232 16.464896,17.71232 C17.393536,17.71232 18.146816,18.4656 18.146816,19.39424"
                id="Fill-32"
                fill="#D9DFE4"
              />
              <path
                d="M32.929984,27.944256 C32.929984,28.872896 32.176704,29.626176 31.248064,29.626176 C30.319424,29.626176 29.566144,28.872896 29.566144,27.944256 C29.566144,27.015616 30.319424,26.262336 31.248064,26.262336 C32.176704,26.262336 32.929984,27.015616 32.929984,27.944256"
                id="Fill-33"
                fill="#D9DFE4"
              />
              <path
                d="M3.363712,27.944256 C3.363712,28.872896 2.611072,29.626176 1.681792,29.626176 C0.753152,29.626176 -0.000128,28.872896 -0.000128,27.944256 C-0.000128,27.015616 0.753152,26.262336 1.681792,26.262336 C2.611072,26.262336 3.363712,27.015616 3.363712,27.944256"
                id="Fill-34"
                fill="#D9DFE4"
              />
              <path
                d="M3.363712,10.871168 C3.363712,11.799808 2.611072,12.553088 1.681792,12.553088 C0.753152,12.553088 -0.000128,11.799808 -0.000128,10.871168 C-0.000128,9.941888 0.753152,9.189248 1.681792,9.189248 C2.611072,9.189248 3.363712,9.941888 3.363712,10.871168"
                id="Fill-35"
                fill="#D9DFE4"
              />
              <path
                d="M18.146816,2.321792 C18.146816,3.251072 17.393536,4.003712 16.464896,4.003712 C15.536256,4.003712 14.782976,3.251072 14.782976,2.321792 C14.782976,1.393152 15.536256,0.639872 16.464896,0.639872 C17.393536,0.639872 18.146816,1.393152 18.146816,2.321792"
                id="Fill-36"
                fill="#D9DFE4"
              />
              <path
                d="M18.146816,36.466752 C18.146816,37.396032 17.393536,38.148672 16.464896,38.148672 C15.536256,38.148672 14.782976,37.396032 14.782976,36.466752 C14.782976,35.538112 15.536256,34.784832 16.464896,34.784832 C17.393536,34.784832 18.146816,35.538112 18.146816,36.466752"
                id="Fill-37"
                fill="#D9DFE4"
              />
              <polygon
                id="Stroke-38"
                stroke="#D9DFE4"
                strokeWidth="0.64"
                points="1.681856 10.871168 16.465216 19.394048 16.465216 36.466688 1.681856 27.944448"
              />
              <polygon
                id="Stroke-39"
                stroke="#D9DFE4"
                strokeWidth="0.64"
                points="1.681856 10.871168 16.465216 2.322048 31.247936 10.871168 16.465216 19.394048"
              />
              <polygon
                id="Stroke-40"
                stroke="#D9DFE4"
                strokeWidth="0.64"
                points="31.248064 27.944256 31.248064 10.870976 16.464704 19.394496 16.464704 36.466496"
              />
            </g>
          </g>
        </g>
      </g>
    </svg>
  );
};