rm ./data/result*
cp ../results/result.csv ./data/result.csv

python parse.py
python -m SimpleHTTPServer 8000

# open http://localhost:8000