# Google Page Rank

Using MapReduce to implement and visualize Google PageRank Algorithm

<div align="center">
  <img src="https://user-images.githubusercontent.com/13844740/33436934-692d9412-d621-11e7-8bc1-b2213eeb3289.gif" height="374px">
</div>
<div align="center">
  <a href="https://youtu.be/VIeWJLxWAqc">Watch Full Demo Video</a>
</div>

## Description
This is an implementation for [The PageRank Citation Ranking: Bringing Order to the Web](http://ilpubs.stanford.edu:8090/422/1/1999-66.pdf).<br/>Using MapReduce to handle multiplication of very large matrices and optimize computation.<br/>
- Step 1: Use <b>adjacency matrix</b> to construct/describe relations between any two of all web pages, which is called <b>Transition Matrix</b>. Then based on iterative computations, we use the <b>Transition Matrix</b> to compute the <b>PageRank</b> of each web page util the <b>PageRank Matrix</b> converged. NOTE: we use <b>BETA</b> to handle two edge cases - <b>Dead Ends</b> and <b>Spider Traps</b>.
- step 2: Use the converged <b>PageRank Matrix</b> we got from step 1 to rank all web pages and visulize the results.

## Prerequisites
The test data of this repo which is put in the folder `dataset` comes from [here](https://www.limfinity.com/ir/).<br/>You can use any web pages data as you want.
- `transition.txt` each line describes one web page can link to a list of web pages.<br/>For example, one line "1 \t 2,3,4,5" means a web page whose ID is 1 can link to four web pages whose IDs are 2, 3, 4 and 5 respectly.
- `pr.txt` describes initial PageRank values of all web pages.<br/>For example, one line "57 \t 1" means the initial PageRank value of a web page whose ID is 57 is 1.<br/>Besides, PageRank values of all web pages are uniformly intialized to 1.

## Manual
- Copy the repo to the hadoop-clusters (or you can simply git clone the repo in the hadoop-master).
- Run the <b>MapReduce</b> jobs to get <b>PageRank</b> data for all web pages and converte the <b>PageRank</b> data to the JSON format for visualization:</br>
<b>(1)</b> `cd` to the folder `map_reduce_job`<br/><b>(2)</b> Run the script `run_mapreduce.sh`<br/>(This script will automatically handle all the preparation work, compile all the `java` files and run MapReduce jobs).<br/>You will get `PageRank` data in the path `../results/pr30.txt`<br/>(Here, 30 means the program iterates 30 times. You can modify this parameter in the `run_mapreduce.sh`).<br/><b>(3)</b> Run the script `converte_data.sh`<br/>(This script will automatically compile the `DataConverter.java` file and run it to converte the raw `PageRank` data which you get from previous step to a CSV file).<br/>You will get a CSV file in the path `../results/result.csv` which will be parsed to a JSON file later.<br/><b>(4)</b> Now you can exit hadoop-clusters environment, and `cd` to the folder `web_interface`.<br/><b>(5)</b> Run the script `run.sh`<br/>(This script will automatically converte the `result.csv` file which you get from previous step to a JSON file).<br/>You will get a JSON file in the path `./data/result.json`<br/>By the way, the script will automatically open `http://localhost:8000` on a browser and display the `PageRank Visualization` for you.

## WorkFlow
Dataset --> <br/>PageRank Matrix data (<b>Hadoop/MapReduce</b>) --> <br/>CSV data (<b>Java</b>) --> <br/>JSON data (<b>Python</b>) --> <br/>Web Interface (<b>JavaScript</b> and <b>HTML</b>)

## Citation
```
  @misc{ye2017googlepagerank,
    author = {Wengao Ye},
    title = {Google Page Rank},
    year = {2017},
    publisher = {GitHub},
    journal = {GitHub repository},
    howpublished = {\url{https://github.com/elleryqueenhomels/google_page_rank}}
  }
```
