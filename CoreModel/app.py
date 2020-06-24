from flask import Flask, jsonify
from extract_features import extract_feature
from caption import get_caption
from vqa import get_answer
from flask import request
import os
from werkzeug.utils import secure_filename

app = Flask(__name__)
basedir=os.path.abspath(os.path.dirname(__file__))

@app.route('/caption', methods=["GET", "POST"])
def caption():
    img = request.files['file']
    print('连接成功')
	
    basepath = os.path.dirname(__file__)
    upload_path = os.path.join(basepath, 'images', secure_filename(img.filename))
    print(upload_path)
    img.save(upload_path)
    
    print("generating captions from image...")
    words = get_caption(upload_path)
    print("captions is :"+str(words))
    print("done")
    print()

#    return jsonify(words)
    return str(words)

@app.route('/feature')
def feature():
    print("extracting features from draw image...")
    extract_feature()
    print("done")
    print()

@app.route('/vqa')
def vqa(question="How many cars are there?"):
    print("getting answer for specific question...")
    answer = get_answer(question)
    print("Question is :" + str(question))
    print("Answer   is :" + str(answer))
    print("done")
    print()
    return jsonify(answer)


if __name__ == '__main__':
    print("== Test functions")
    print()

    print("=== Test caption...")
    print("caption:" + str(get_caption()))
    print("done")
    print()

    print("=== Test feature_extracting...")
    extract_feature()
    print("done")
    print()

    print("=== Test vqa...")
    print("vqa:" + str(get_answer(question="How many cars are there?")))
    print("done")
    print()
    app.debug = False
    # app.run(host='124.70.139.138', port=5000)
    app.run(host='222.19.197.230', port=5000)
	
#实际用的GPU代码与此模型代码有区别，应用还需修改。