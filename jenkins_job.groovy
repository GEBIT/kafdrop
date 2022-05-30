import de.gebit.build.jenkins.MavenJobBuilder

def builder = new MavenJobBuilder(binding) {
	
	/**
	 * Configure binding for Google Cloud Repository credetials.
	 */
	protected void configureJobBasics() {
		super.configureJobBasics()
		job.configure { project ->
			project / 'buildWrappers' / 'org.jenkinsci.plugins.credentialsbinding.impl.SecretBuildWrapper' / 'bindings' << 'org.jenkinsci.plugins.credentialsbinding.impl.UsernamePasswordMultiBinding' {
				usernameVariable('EU_GCR_IO_RP_USERNAME')
				passwordVariable('EU_GCR_IO_RP_PASSWORD')
				credentialsId('eu-gcr-io')
			}
			project / 'buildWrappers' / 'org.jenkinsci.plugins.credentialsbinding.impl.SecretBuildWrapper' / 'bindings' << 'org.jenkinsci.plugins.credentialsbinding.impl.UsernamePasswordMultiBinding' {
				usernameVariable('AWS_ACCESS_KEY_ID')
				passwordVariable('AWS_SECRET_ACCESS_KEY')
				credentialsId('100453121084-dkr-ecr-eu-central-1-amazonaws-com')
			}
			project / 'buildWrappers' / 'org.jenkinsci.plugins.credentialsbinding.impl.SecretBuildWrapper' / 'bindings' << 'org.jenkinsci.plugins.credentialsbinding.impl.UsernamePasswordMultiBinding' {
				usernameVariable('GEBITHUB_USERNAME')
				passwordVariable('GEBITHUB_PASSWORD')
				credentialsId('gebithub-gpo-robot')
			}
		}
	}
}
