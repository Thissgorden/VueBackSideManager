import axios from 'axios'
import router from './router'
import Element from 'element-ui'

axios.defaults.baseURL = "http://localhost:8081"

const request = axios.create({
    timeout: 5000,//超时时间5秒
    //配置请求头
    headers: {
        'Content-Type': 'application/json; charset=utf-8'
    }
});
//发起请求的时候这个方法会拦截请求添加jwt后放行

request.interceptors.request.use(config => {
    config.headers['Authorization'] = localStorage.getItem("token");
    return config;//如果没有return就会直接拦下整个请求
});


request.interceptors.response.use(response => {
    let res = response.data
    if (res.code === 200) {
        return response;
    } else {
        Element.Message.error(res.msg ? res.msg : '系统异常！', {duration: 3 * 1000})
        return Promise.reject(response.data.msg);
    }
}, error => {
    if (error.response.data) {
        error.message = error.response.data.msg
    }

    if (error.response.status === 401) {
            router.push("/login")
        }


    Element.Message.error(error.message, {duration: 3 * 1000})


return Promise.reject(error)

}
)
export default request