const getApiBaseUrl = () => {
  const windowLocation = window.location
  const baseUrl = windowLocation.pathname + '//' + windowLocation.host
  return baseUrl + '/usww/api'
}

export const environment = {
  apiUrl: '/usww/api'
};
