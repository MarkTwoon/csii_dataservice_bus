package com.csii.regist.config;

import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlBlockHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.csii.commons.entities.CommonResult;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.alibaba.fastjson.JSON;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Component
@ControllerAdvice
@ResponseBody
public class SentinelExceptionHandler implements UrlBlockHandler {
    @Override
    public void blocked(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BlockException e) throws IOException {
        String msg="";
        if (e instanceof FlowException) {
            msg = "接口被限流了";
        } else if (e instanceof DegradeException) {
            msg = "接口被降级了";
        } else if (e instanceof ParamFlowException) {
            msg = "接口被热点参数限流";
        } else if (e instanceof SystemBlockException) {
            msg = "接口被系统规则限流或降级";
        } else if (e instanceof AuthorityException) {
            msg = "接口被授权规则不通过";
        }
        CommonResult data = new CommonResult(500,msg);
        httpServletResponse.setCharacterEncoding("utf-8");
        httpServletResponse.setContentType("application/json;charset=utf-8");
        httpServletResponse.setHeader("Content-Type", "application/json;charset=utf-8");
        httpServletResponse.getWriter().write(JSON.toJSONString(data));
    }

    /**
     * 处理流量控制异常处理
     */
    @ExceptionHandler({ParamFlowException.class})
    public CommonResult handleRRException(ParamFlowException e){

        return new CommonResult(500, "接口被热点参数限流");
    }


}
