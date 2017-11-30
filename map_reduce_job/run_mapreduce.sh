hdfs dfs -rm -r /transition
hdfs dfs -mkdir /transition

hdfs dfs -put ../dataset/transition.txt /transition

hdfs dfs -rm -r /output*
hdfs dfs -rm -r /pagerank*
hdfs dfs -mkdir /pagerank0

hdfs dfs -put ../dataset/pr.txt /pagerank0

hadoop com.sun.tools.javac.Main *.java
jar cf pagerank.jar *.class

hadoop jar pagerank.jar Driver /transition /pagerank /output 30 0.2