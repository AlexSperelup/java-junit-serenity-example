Automation tests

As example, get results from 2 different search engines

To run tests:
1. Make sure you have Chrome v102 installed (latest one)
2. Open project in Idea
3. Open terminal
4. Execute command "mvn clean test serenity:aggregate"
5. Wait until tests are finished
6. See in output path to reports with detailed steps
Example:
[INFO] SERENITY REPORTS
[INFO]   - Full Report: file:///C:/data/test-automation/target/site/serenity/index.html
[INFO]   - Single Page HTML Summary: file:///C:/data/test-automation/target/site/serenity/serenity-summary.html

Project might be started on remote Selenium grid and in Selenoid with Docker
