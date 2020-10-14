#!/usr/bin/env bash

# 训练NLU和Core模型
python -m rasa train --config configs/config.yml --domain configs/domain.yml --data data/

python -m rasa train --config configs/zh_jieba_supervised_embeddings_config.yml --domain configs/domain.yml --data data/

python -m rasa train --config configs/zh_jieba_mitie_embeddings_config.yml --domain configs/domain.yml --data data/

#usage: rasa train [-h] [-v] [-vv] [--quiet] [--data DATA [DATA ...]]
#                  [-c CONFIG] [-d DOMAIN] [--out OUT]
#                  [--augmentation AUGMENTATION] [--debug-plots]
#                  [--dump-stories] [--fixed-model-name FIXED_MODEL_NAME]
#                  [--persist-nlu-data] [--force]
#                  {core,nlu} ...

# 交互式学习构建样本
python -m rasa run actions --port 5055 --actions actions --debug
python -m rasa interactive -m models/20200319-162558.tar.gz --endpoints configs/endpoints.yml --config configs/zh_jieba_mitie_embeddings_config.yml
