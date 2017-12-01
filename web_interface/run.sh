rm ./data/result*
cp ../results/result.csv ./data/result.csv

python parse.py

create_http_server() {
    python -m SimpleHTTPServer 8000
}

create_http_server &

sleep 1
open http://localhost:8000
