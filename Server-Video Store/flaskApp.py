from flask import Flask, request
import os

app = Flask(__name__) 

@app.route("/") 
def index(): 
   return "Welcome to FlaskApp" 

@app.route("/upload_video", methods = ['POST'])
def upload_video():
    if request.files:

        video = request.files['gestureVideo']

        print(video)

        video.save(os.path.join("/Users/yash/files/video_store", video.filename))

        return "video recieved"


if __name__ == '__main__': 
   app.run(host="0.0.0.0",port=5000, debug=True) # application will start listening for web request on port 5000