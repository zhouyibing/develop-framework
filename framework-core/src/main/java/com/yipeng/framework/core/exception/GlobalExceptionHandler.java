package com.yipeng.framework.core.exception;

import cn.hutool.core.collection.CollectionUtil;
import com.yipeng.framework.core.constants.Constants;
import com.yipeng.framework.core.model.biz.ContextHolder;
import com.yipeng.framework.core.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	@ResponseBody
	@ExceptionHandler
	public Result<?> processException(Throwable e) {
		log.error("unknown exception！", e);
		clearContext();
		return Result.fail(ErrorCode.SERVER_INTERNAL_ERROR.errorParams(new Object[]{e.getMessage()}));
	}

	@ResponseBody
	@ExceptionHandler
	public Result<?> processException(DataAccessException e) {
		log.error("db exception！", e);
		clearContext();
		if(e instanceof DuplicateKeyException) {
			return Result.fail(ErrorCode.DUPLICATE_KEY.errorParams(new Object[]{e.getCause().getMessage()}));
		} else {
			return Result.fail(ErrorCode.DATA_ACCESS_EXCEPTION.errorParams(new Object[]{e.getCause().getMessage()}));
		}
	}
	
	@ResponseBody
	@ExceptionHandler
	public Result<?> processException(ExceptionUtil.BizException e) {
		ErrorCode errorCode = e.getErrorCode();
		Result<?> response = Result.fail(errorCode);
		log.error("business exception！(" + errorCode.getCode()+")" , e);
		clearContext();
		return response;
	}

	@ResponseBody
	@ExceptionHandler
	public Result<?> processException(ExceptionUtil.TechException e) {
		ErrorCode errorCode = e.getErrorCode();
		Result<?> response = Result.fail(errorCode);
		log.error("tech exception！(" + errorCode.getCode()+")" , e);
		clearContext();
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
		clearContext();
		return response;
	}

	public void clearContext() {
		ContextHolder.removeCallContext();
		MDC.remove(Constants.HEAD_TRACEID);
	}
}
