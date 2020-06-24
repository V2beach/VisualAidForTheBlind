## installation & requirement

-  env & requirements

  ```
  Python 3.6.4 (建议anoconda)
  pytorch 0.4.0
  
  cython
  cffi
  opencv-python
  scipy
  msgpack
  easydict
  matplotlib
  pyyaml
  tensorboardX
  tqdm
  torchvision
  numpy
  ```

- install

  1. clone the code and unzip

  2. compile the code

     ```
     cd lib
     sh make.sh
     ```

  3. install Spacy and initialize Glove

     ```
     dowanload en_vectors_web_lg-2.1.0.tar.gz
     pip install en_vectors_web_lg-2.1.0.tar.gz
     ```

  4. unzip pretrained models to 'ckpt' directory

## usage

- get image

  ```
  |-- ckpt # 预训练的模型及配置
  |-- features
  	|-- images.npy # run extrac_features 生成的image.jpg 图片特征 用于进行问答
  |-- images
  	|-- image.jpg # 图片存放位置，默认名称image
  |-- lib # extract_features.py 的依赖文件
  |-- models
  	|-- vqa # vqa.py 的依赖模型
  |-- tools
  	|-- caption.py # image caption 的主程序
  |-- extract_features.py # 提取图片信息的主程序
  	vqa.py # 问答的主程序
  ```

- image caption

  `python tools/caption.py`

- feature extraction

  `python extract_features.py `

- question answering

  `python vqa.py`

  