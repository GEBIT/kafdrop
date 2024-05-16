library changelog: false, identifier: "gebit-jenkins@${GEBIT_BUILD_BRANCH}"

// add additional custom credentials for integration jobs
pipelineStepHook.after('bindCredentials', {bindings ->
    if (pipelineContext.jobType() == 'integration') {
        bindings << usernamePassword(credentialsId: 'eu-gcr-io', usernameVariable: 'EU_GCR_IO_RP_USERNAME', passwordVariable: 'EU_GCR_IO_RP_PASSWORD')
        bindings << usernamePassword(credentialsId: '100453121084-dkr-ecr-eu-central-1-amazonaws-com', usernameVariable: 'AWS_ACCESS_KEY_ID', passwordVariable: 'AWS_SECRET_ACCESS_KEY')
        bindings << usernamePassword(credentialsId: 'gebithub-gpo-robot', usernameVariable: 'GEBITHUB_USERNAME', passwordVariable: 'GEBITHUB_PASSWORD')                
    }
    return bindings
})
