import React from 'react'
import ReactDOM from 'react-dom'
// import './index.css';
import App from './App'
import * as serviceWorker from './serviceWorker'
import StoreProvider from 'store'

import i18n from 'modules/i18n'
import moment from 'moment'

moment.locale(i18n.language)

ReactDOM.render(
  <StoreProvider>
    <App />
  </StoreProvider>,
  document.getElementById('root'),
)

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister()
