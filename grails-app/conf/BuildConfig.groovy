grails.project.work.dir = 'target'
grails.project.source.level = 1.6
grails.project.docs.output.dir = 'docs/manual' // for backwards-compatibility, the docs are checked into gh-pages branch

grails.project.dependency.resolution = {

	inherits 'global', {
		excludes 'grails-plugin-log4j', 'log4j'
	}
	log 'warn'

	repositories {
		grailsCentral()
		mavenLocal()
		mavenCentral()
	}

	dependencies {
		String dropwizardVersion = '0.6.1'
		String jacksonVersion = '2.1.1'
		String metricsVersion = '2.2.0'
		String jerseyVersion = '1.15'
		String slf4jVersion = '1.7.2'
		String jettyVersion = '8.1.8.v20121106'

		compile "org.slf4j:slf4j-api:$slf4jVersion", {
			excludes 'junit'
		}
		compile "org.slf4j:jcl-over-slf4j:$slf4jVersion", {
			excludes 'junit', 'slf4j-api', 'slf4j-jdk14'
		}
		compile "org.slf4j:jul-to-slf4j:$slf4jVersion", {
			excludes 'junit', 'slf4j-api', 'slf4j-log4j12'
		}

		compile "com.yammer.dropwizard:dropwizard-core:$dropwizardVersion", {
			excludes 'argparse4j', 'fest-assert-core', 'guava', 'hibernate-validator', 'jackson-databind', 'jackson-dataformat-yaml',
			         'jackson-datatype-guava', 'jackson-datatype-joda', 'jackson-jaxrs-json-provider', 'jersey-core', 'jersey-server',
			         'jersey-servlet', 'jersey-test-framework-core', 'jersey-test-framework-inmemory', 'jetty-http', 'jetty-server',
			         'jetty-servlet', 'joda-time', 'jsr305', 'jul-to-slf4j', 'junit', 'log4j-over-slf4j', 'logback-classic',
			         'logback-core', 'metrics-core', 'metrics-jersey', 'metrics-jetty', 'metrics-logback', 'metrics-servlet',
			         'mockito-all', 'slf4j-api', 'test-jetty-servlet'
		}

		compile "com.fasterxml.jackson.core:jackson-databind:$jacksonVersion", {
			excludes 'cglib', 'groovy', 'hibernate-cglib-repack', 'jackson-annotations', 'jackson-core', 'junit'
		}

		compile "com.fasterxml.jackson.datatype:jackson-datatype-joda:$jacksonVersion", {
			excludes 'jackson-core', 'jackson-databind', 'joda-time', 'junit'
		}

		compile "com.fasterxml.jackson.jaxrs:jackson-jaxrs-json-provider:$jacksonVersion", {
			excludes 'jackson-core', 'jackson-databind', 'jackson-module-jaxb-annotations', 'jersey-core',
			         'jersey-server', 'jsr311-api', 'junit'
		}

		compile "com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion", {
			excludes 'jackson-annotations', 'jackson-core', 'jackson-databind', 'junit'
		}

		compile "com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion"

		compile "com.fasterxml.jackson.core:jackson-core:$jacksonVersion", {
			excludes 'junit'
		}

		compile "com.fasterxml.jackson.datatype:jackson-datatype-guava:$jacksonVersion", {
			excludes 'guava', 'jackson-core', 'jackson-databind', 'junit'
		}

		compile 'javax.ws.rs:jsr311-api:1.1.1', {
			excludes 'junit'
		}

		compile "com.yammer.dropwizard:dropwizard-views:$dropwizardVersion", {
			excludes 'compiler', 'dropwizard-core', 'fest-assert-core', 'freemarker', 'junit', 'mockito-all'
		}
		compile 'org.freemarker:freemarker:2.3.19'
		compile "com.github.spullara.mustache.java:compiler:0.8.8", {
			excludes 'guava', 'jackson-mapper-asl', 'jruby', 'junit', 'scala-library', 'util-core'
		}

		compile "com.yammer.metrics:metrics-core:$metricsVersion", {
			excludes 'hamcrest-all', 'junit', 'mockito-all', 'slf4j-api'
		}
		compile "com.yammer.metrics:metrics-annotation:$metricsVersion", {
			excludes 'hamcrest-all', 'junit', 'mockito-all'
		}
		compile "com.yammer.metrics:metrics-jersey:$metricsVersion", {
			excludes 'hamcrest-all', 'jersey-server', 'jersey-test-framework-inmemory',
			         'junit', 'metrics-annotation', 'metrics-core', 'mockito-all'
		}
		compile "com.yammer.metrics:metrics-jetty:$metricsVersion", {
			excludes 'hamcrest-all', 'jetty-server', 'junit', 'metrics-core', 'mockito-all'
		}
		compile "com.yammer.metrics:metrics-logback:$metricsVersion", {
			excludes 'hamcrest-all', 'junit', 'logback-classic', 'logback-core', 'metrics-core', 'mockito-all'
		}
		compile "com.yammer.metrics:metrics-servlet:$metricsVersion", {
			excludes 'hamcrest-all', 'jackson-databind', 'jetty-servlet', 'junit',
			         'metrics-core', 'metrics-jetty', 'mockito-all', 'servlet-api'
		}

		compile 'com.google.guava:guava:13.0.1', {
			excludes 'jsr305'
		}

		compile "com.sun.jersey:jersey-core:$jerseyVersion", {
			excludes 'jaxb-api', 'jsr311-api', 'junit', 'mail', 'org.osgi.core'
		}

		compile "com.sun.jersey:jersey-server:$jerseyVersion", {
			excludes 'asm', 'commons-io', 'jaxb-api', 'jsr250-api', 'junit', 'mail', 'osgi_R4_core'
		}

		compile "com.sun.jersey:jersey-servlet:$jerseyVersion", {
			excludes 'ant', 'commons-io', 'javax.ejb', 'javax.servlet-api', 'jsp-api', 'junit',
			         'osgi_R4_core', 'persistence-api', 'weld-osgi-bundle'
		}

		compile 'joda-time:joda-time:2.1', {
			excludes 'joda-convert', 'junit'
		}

		compile 'javax.validation:validation-api:1.0.0.GA'

		compile 'org.hibernate:hibernate-validator:4.1.0.Final', {
			excludes 'easymock', 'h2', 'hibernate-entitymanager', 'hibernate-jpa-2.0-api', 'jaxb-api',
			         'jaxb-impl', 'slf4j-api', 'slf4j-log4j12', 'testng', 'validation-api'
		}

		compile "org.eclipse.jetty:jetty-server:$jettyVersion", {
			excludes 'javax.servlet', 'jetty-continuation', 'jetty-http', 'jetty-jmx', 'jetty-test-helper', 'mockito-core'
		}
		compile "org.eclipse.jetty:jetty-servlet:$jettyVersion", {
			excludes 'jetty-jmx', 'jetty-security', 'jetty-test-helper'
		}
		compile "org.eclipse.jetty:jetty-http:$jettyVersion", {
			excludes 'javax.servlet', 'jetty-io', 'junit'
		}
		compile "org.eclipse.jetty:jetty-continuation:$jettyVersion", {
			excludes 'javax.servlet', 'jetty-util'
		}
		compile "org.eclipse.jetty:jetty-jmx:$jettyVersion", {
			excludes 'jetty-util', 'junit'
		}
		compile "org.eclipse.jetty:jetty-security:$jettyVersion", {
			excludes 'jetty-server', 'junit'
		}
		compile "org.eclipse.jetty:jetty-io:$jettyVersion", {
			excludes 'jetty-test-helper', 'jetty-util'
		}
		compile "org.eclipse.jetty:jetty-util:$jettyVersion", {
			excludes 'javax.servlet', 'jetty-test-helper', 'slf4j-api', 'slf4j-jdk14'
		}
		compile "org.eclipse.jetty:jetty-webapp:$jettyVersion", {
			excludes 'jetty-jmx', 'jetty-servlet', 'jetty-xml', 'junit'
		}
		compile "org.eclipse.jetty:jetty-xml:$jettyVersion", {
			excludes 'jetty-util', 'junit'
		}
		compile "org.eclipse.jetty:jetty-jndi:$jettyVersion", {
			excludes 'javax.mail.glassfish', 'jetty-server', 'junit'
		}
		compile "org.eclipse.jetty:jetty-plus:$jettyVersion", {
			excludes 'derby', 'javax.transaction', 'jetty-jndi', 'jetty-webapp', 'junit'
		}
		compile "org.eclipse.jetty:jetty-servlets:$jettyVersion", {
			excludes 'javax.servlet', 'jetty-client', 'jetty-continuation', 'jetty-test-helper',
			         'jetty-util', 'jetty-webapp', 'junit', 'test-jetty-servlet'
		}
		compile "org.eclipse.jetty:jetty-websocket:$jettyVersion", {
			excludes 'javax.servlet', 'jetty-http', 'jetty-io', 'jetty-server',
			         'jetty-servlet', 'jetty-test-helper', 'jetty-util'
		}

		compile('org.glassfish:javax.ejb:3.1') {
			excludes 'javax.annotation', 'javax.transaction', 'javax.xml.rpc', 'junit'
		}

		compile 'org.grails.plugins:logback:0.2'
	}

	plugins {
		build ':release:2.2.1', ':rest-client-builder:1.0.3', {
			export = false
		}
	}
}
