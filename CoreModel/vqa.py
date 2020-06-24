import torch
import pickle
import numpy as np
import argparse, yaml
from vqa_models.net import Net
from tools.base_cfgs import Cfgs
from tools.data_utils import proc_img_feat, proc_ques


def parse_args():
    '''
    Parse input arguments
    '''
    parser = argparse.ArgumentParser(description='VQA Args')

    parser.add_argument('--model', dest='model',
                        type=str)

    parser.add_argument('--feature',
                        type=str)

    parser.add_argument('--image',
                        help='vqav2 dataset root path',
                        type=str)

    parser.add_argument('--seed', dest='seed',
                        help='fix random seed',
                        type=int)

    # parser.add_argument('--CKPT_V', dest='CKPT_VERSION',
    #                     help='checkpoint version',
    #                     type=str)
    #
    # parser.add_argument('--CKPT_E', dest='CKPT_EPOCH',
    #                     help='checkpoint epoch',
    #                     type=int)

    args = parser.parse_args()
    return args


def get_answer(question):
    __C = Cfgs()
    args = parse_args()

    args.model = 'vqa_model'
    args.image = 'timg_1'
    args.feature = 'timg_1'

    cfg_file = "ckpts/{}.yml".format(args.model)
    with open(cfg_file, 'r') as f:
        model_cfg = yaml.load(f)
    # model_cfg = argparse.Namespace(**model_cfg)
    __C.add_args(model_cfg)
    __C.proc()
    # print(__C)
    image_cfg = "images/{}.jpg".format(args.image)
    feature_cfg = "features/{}.npy".format(args.feature)

    model_path = 'ckpts/vqa.pkl'
    state_dict = torch.load(model_path)['state_dict']

    pickle_r = open("ckpts/vqa_word_info.pkl", "rb")
    data = pickle.load(pickle_r)

    token_to_ix, pretrained_emb, ans_to_ix, ix_to_ans = \
        data["token_to_ix"], data["pretrained_emb"], data["ans_to_ix"], data["ix_to_ans"]
    token_size = token_to_ix.__len__()
    ans_size = ans_to_ix.__len__()

    net = Net(
        __C,
        pretrained_emb,
        token_size,
        ans_size
    )

    net.eval()
    net.load_state_dict(state_dict)

    img_feat_iter, ques_ix_iter = [], []
    # question = {"question": "How many are cars?"}
    question = {"question": question}
    img_feat_iter.append(proc_img_feat(np.load(feature_cfg), __C.IMG_FEAT_PAD_SIZE))
    ques_ix_iter.append(proc_ques(question, token_to_ix, __C.MAX_TOKEN))

    img_feat_iter = torch.from_numpy(np.array(img_feat_iter))
    ques_ix_iter = torch.from_numpy(np.array(ques_ix_iter))

    pred = net(
        img_feat_iter,
        ques_ix_iter
    )

    pred_np = pred.data.numpy()
    pred_argmax = np.argmax(pred_np, axis=1)

    ans_ix_list = []
    ans_ix_list.append(pred_argmax)
    ans_ix_list = np.array(ans_ix_list).reshape(-1)

    # result = [{
    #     'answer': ix_to_ans[str(ans_ix_list[qix])],
    #     'question': str(question[qix]['question'])
    # } for qix in range(question.__len__())]
    result = {
            'answer': ix_to_ans[str(ans_ix_list[0])],
            'question': str(question['question'])
        }

    # print(result)
    return result["answer"]

if __name__ == '__main__':
    __C = Cfgs()
    args = parse_args()

    args.model = 'vqa_model'
    args.image = 'image'
    args.feature = 'image'

    cfg_file = "ckpts/{}.yml".format(args.model)
    with open(cfg_file, 'r') as f:
        model_cfg = yaml.load(f)
    # model_cfg = argparse.Namespace(**model_cfg)
    __C.add_args(model_cfg)
    __C.proc()
    print(__C)
    image_cfg = "images/{}.jpg".format(args.image)
    feature_cfg = "features/{}.npy".format(args.feature)

    model_path = 'ckpts/vqa.pkl'
    state_dict = torch.load(model_path)['state_dict']

    pickle_r = open("ckpts/vqa_word_info.pkl", "rb")
    data = pickle.load(pickle_r)

    token_to_ix, pretrained_emb, ans_to_ix, ix_to_ans = \
        data["token_to_ix"], data["pretrained_emb"],data["ans_to_ix"],data["ix_to_ans"]
    token_size = token_to_ix.__len__()
    ans_size = ans_to_ix.__len__()

    net = Net(
        __C,
        pretrained_emb,
        token_size,
        ans_size
    )

    net.eval()
    net.load_state_dict(state_dict)
    # net.cuda()

    # save orginal gpu version to cpu
    # net.cpu()
    # state_dict_cpu = {"state_dict":net.state_dict()}
    # torch.save(state_dict_cpu ,"vqa.pkl")

    img_feat_iter, ques_ix_iter = [], []
    question = {"question": "How many are cars?"}
    img_feat_iter.append(proc_img_feat(np.load(feature_cfg), __C.IMG_FEAT_PAD_SIZE))
    ques_ix_iter.append(proc_ques(question, token_to_ix, __C.MAX_TOKEN))

    img_feat_iter = torch.from_numpy(np.array(img_feat_iter))
    ques_ix_iter = torch.from_numpy(np.array(ques_ix_iter))

    pred = net(
        img_feat_iter,
        ques_ix_iter
    )

    pred_np = pred.data.numpy()
    pred_argmax = np.argmax(pred_np, axis=1)

    ans_ix_list = []
    ans_ix_list.append(pred_argmax)
    ans_ix_list = np.array(ans_ix_list).reshape(-1)

    result = [
        {
            'answer': ix_to_ans[str(ans_ix_list[0])],
            'question': str(question['question'])
        }
    ]

    print(result)