package org.dieschnittstelle.jee.esa.skeleton.ejb.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.transaction.Transactional;

import org.apache.log4j.Logger;

@Interceptor
@Transactional
public class LoggingInterceptor {

	protected static Logger logger = Logger.getLogger(LoggingInterceptor.class);

	/*
	 * a map of loggers
	 */
	private static Map<Class<?>, Logger> loggers = Collections.synchronizedMap(new HashMap<Class<?>, Logger>());

	/**
	 * obtain a logger
	 */
	private Logger getLogger(Class<?> klass) {
		if (loggers.containsKey(klass))
			return loggers.get(klass);
		return createNewLogger(klass);
	}

	private synchronized Logger createNewLogger(Class<?> klass) {
		logger.info("createNewLogger(): class is: " + klass);

		Logger logger = Logger.getLogger(klass);
		loggers.put(klass, logger);

		return logger;
	}

	/**
	 * log a method invocation
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	@AroundInvoke
	public Object logMethod(final InvocationContext context) throws Exception {

		StringBuffer buf = new StringBuffer();
		String prefix = context.getMethod().getName() + "()";

		/*
		 * log the input
		 */
		buf.append(prefix);
		buf.append(": invoke with arguments: (");

		for (int i = 0; i < context.getParameters().length; i++) {
			buf.append(context.getParameters()[i]);
			if (i < (context.getParameters().length - 1)) {
				buf.append(", ");
			}
		}
		buf.append(")");

		getLogger(context.getTarget().getClass()).info(buf.toString());

		/*
		 * execute the method
		 */
		Object result = context.proceed();

		/*
		 * log the output
		 */
		buf.setLength(0);
		buf.append(prefix);

		// check whether we have a void method
		if (context.getMethod().getReturnType() == Void.TYPE) {
			buf.append(": returned");
		} else {
			buf.append(": got return value: ");
			buf.append(result);
		}

		getLogger(context.getTarget().getClass()).info(buf.toString());

		return result;
	}

}
