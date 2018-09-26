pipeline {
    agent {
      docker {
        image 'plinzen/android:latest'
        // for android dev
        label 'android'
      }
    }

    options {
        // use gitlab
        gitLabConnection('gitlab')
        gitlabCommitStatus(name: 'jenkins')
    }

    triggers {
        // use gitlab
        gitlab(triggerOnPush: true, triggerOnMergeRequest: true, branchFilterType: 'All')
    }
    // create each step 
    stages {
        stage('build') {
            steps {
                //checkout([$class: 'GitSCM', branches: [[name: "*/${params.buildBranch}"]], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'SubmoduleOption', disableSubmodules: false, parentCredentials: false, recursiveSubmodules: true, reference: '', trackingSubmodules: false]], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '47a165bb-a245-4844-bdff-3d3601126448', url: 'git@git.ta.dropya.net:sunflower-demo-android/sunflower-demo-android-library-android.git']]])
                // 'checkout scm’ is only available when using “Multibranch Pipeline” or “Pipeline script from SCM”
                checkout scm
                sh 'chmod +x ./gradlew'
                sh './gradlew clean assembleDebug'
                warnings canComputeNew: false, canResolveRelativePaths: false, consoleParsers: [[parserName: 'Java Compiler (javac)']], defaultEncoding: '', excludePattern: '', healthy: '', includePattern: '', messagesPattern: '', unHealthy: ''
                stash includes: '**/build/**', name: 'sunflower-demo-android-build', useDefaultExcludes: false
            }
        }
        stage('static-analysis') {
            steps {
              parallel (
                "android-lint" : {
                  unstash 'sunflower-demo-android-build'
                  sh './gradlew lintDebug'
                  stash includes: '**/lint-results-debug.xml', name: 'analysis-lint', useDefaultExcludes: false
                },
                "checkstyle" : {
                  unstash 'sunflower-demo-android-build'
                  sh './gradlew checkstyleReport'
                  stash includes: '**/checkstyle-results.xml', name: 'analysis-checkstyle', useDefaultExcludes: false
                }
              )
            }
        }
        stage('test') {
            steps {
                unstash 'sunflower-demo-android-build'
                sh './gradlew testDebugUnitTest'
                stash includes: '**/build/test-results/testDebugUnitTest/*.xml,', name: 'test', useDefaultExcludes: false
            }
        }

        stage('deploy') {
            when {
                expression {
                    return env.BRANCH_NAME == 'develop' && currentBuild.resultIsBetterOrEqualTo('SUCCESS');
                }
            }
            steps {
                unstash 'sunflower-demo-android-build'
                sh './gradlew crashlyticsUploadDistributionDebug'
            }
        }
    }
    post {
      always {
        unstash 'analysis-lint'
        unstash 'analysis-checkstyle'
        unstash 'test'
        androidLint canComputeNew: false, defaultEncoding: '', healthy: '', pattern: '**/lint-results-debug.xml', unHealthy: ''
        checkstyle canComputeNew: false, defaultEncoding: '', healthy: '', pattern: '**/checkstyle-results.xml', unHealthy: ''
        junit '**/build/test-results/testDebugUnitTest/*.xml'
        addGitLabMRComment comment: currentBuild.resultIsBetterOrEqualTo('SUCCESS') ? ':+1:' : ':-1:'
      }
    }
}
