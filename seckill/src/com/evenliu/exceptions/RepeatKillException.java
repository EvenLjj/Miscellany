package com.evenliu.exceptions;

/**
 * 重复异常
 * (Spring只处理运行时异常，否则不回滚)
 * @author liu
 *
 */
public class RepeatKillException extends SeckillException{

	public RepeatKillException(String message, Throwable cause) {
		super(message, cause);
	}

	public RepeatKillException(String message) {
		super(message);
	}

	
}
