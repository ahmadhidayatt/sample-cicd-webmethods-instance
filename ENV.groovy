environments {
	DEV {
		IntegrationServers {
			is_node1 {
				version = "11.1"
				host = System.getenv('DEV_IS_HOST') ?: "84.247.147.194"
				port = System.getenv('DEV_IS_PORT') ?: "5599"
				username = System.getenv('DEV_IS_USER') ?: "Administrator"
				pwd = System.getenv('DEV_IS_PWD') ?: "manage"
			}
		}
	}
	PROD {
		IntegrationServers {
			is_node1 {
				version = "11.1"
				host = System.getenv('PROD_IS_HOST') ?: "46.250.226.196"
				port = System.getenv('PROD_IS_PORT') ?: "5555"
				username = System.getenv('PROD_IS_USER') ?: "Administrator"
				pwd = System.getenv('PROD_IS_PWD') ?: "m@n@g3"
			}
		}
	}
	TEST {
		IntegrationServers {
			is_node1 {
				version = "11.1"
				host = System.getenv('TEST_IS_HOST') ?: "84.247.147.194"
				port = System.getenv('TEST_IS_PORT') ?: "5599"
				username = System.getenv('TEST_IS_USER') ?: "Administrator"
				pwd = System.getenv('TEST_IS_PWD') ?: "manage"
			}
		}
	}
	QA {
		IntegrationServers {
			is_node1 {
				version = "11.1"
				host = System.getenv('QA_IS_HOST') ?: "84.247.147.194"
				port = System.getenv('QA_IS_PORT') ?: "5599"
				username = System.getenv('QA_IS_USER') ?: "Administrator"
				pwd = System.getenv('QA_IS_PWD') ?: "manage"
			}
		}
	}
}
