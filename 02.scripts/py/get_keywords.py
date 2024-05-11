import numpy as np
from SequenceMatcher import SequenceMatcher 

def get_keywords(pos_list, diary):
    prob_list = []
    similarity_keywords = []

    for i in range(len(pos_list)):
        prob_list.append(round(SequenceMatcher(pos_list[i], diary),3))

    for i in range(5):
        similarity_keywords.append(pos_list[np.argsort(prob_list)[-(i+1)]])

    return similarity_keywords