package com.devops.exceptions;

public class S3Exception extends RuntimeException {

	// Se delega a la clase-padre la instanciación de la excepción
	public S3Exception (Throwable e) {
		super(e);
	}

	public S3Exception (String s) {
		super(s);
	}
}
