package com.yipeng.framework.common.exception;

import cn.hutool.core.collection.CollectionUtil;
import com.yipeng.framework.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	@ResponseBody
	@ExceptionHandler
	public Result<?> processException(Exception e) {
		log.error("unknown exception！", e);
		return Result.fail(ErrorCode.SERVER_INTERNAL_ERROR.errorParams(new Object[]{e.getMessage()}));
	}
	
	@ResponseBody
	@ExceptionHandler
	public Result<?> processException(ExceptionUtil.BizException e) {
		ErrorCode errorCode = e.getErrorCode();
		Result<?> response = Result.fail(errorCode);
		log.error("business exception！(" + errorCode.getCode()+")" , e);
		return response;
	}

	@ResponseBody
	@ExceptionHandler
	public Result<?> processException(ExceptionUtil.TechException e) {
		ErrorCode errorCode = e.getErrorCode();
		Result<?> response = Result.fail(errorCode);
		log.error("tech exception！(" + errorCode.getCode()+")" , e);
		return response;
	}

	/**
	 * 参数验证错误
	 * @param e
	 * @return
	 */
	@ResponseBody
	@ExceptionHandler
	public Result<?> processException(MethodArgumentNotValidException e) {
		String errorMsg = null;
		ErrorCode errorCode = ErrorCode.ILLEGAL_ARGUMENT;
		if(e.getBindingResult() != null && CollectionUtil.isNotEmpty(e.getBindingResult().getAllErrors())){
			errorMsg = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
			errorCode = ErrorCode.ILLEGAL_ARGUMENT.msg(errorMsg);
		}
		Result<?> response = Result.fail(errorCode);
		log.error("valid exception！(" + errorCode.getCode()+")" , e);
		return response;
	}
}
