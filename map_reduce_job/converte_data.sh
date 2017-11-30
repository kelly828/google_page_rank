mkdir ../results
hdfs dfs -get /pagerank30/part-r-00000 ../results/pr30.txt

rm Manifest.txt
echo "Main-Class: DataConverter" > Manifest.txt

jar cfm converter.jar Manifest.txt DataConverter.class
java -jar converter.jar ../dataset/transition.txt ../results/pr30.txt ../results/result.csv