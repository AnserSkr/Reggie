function loginApi(data) {
    return $axios({
      'url': '/user/login',
      'method': 'post',
      data
    })
  }

function loginoutApi() {
  return $axios({
    'url': '/user/loginout',
    'method': 'post',
  })
}

function sendMsgApi(phone) {
    return $axios({
        'url': '/user/sendMsg?phone='+phone,
        'method': 'get',
    })
}

  