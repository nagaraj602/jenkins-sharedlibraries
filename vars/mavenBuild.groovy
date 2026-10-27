def call(String goals = 'clean package -DskipTests') {
    sh "mvn ${goals}"
}
