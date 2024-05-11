import pandas as pd
from keybert import KeyBERT
from kiwipiepy import Kiwi
from transformers import BertModel

def extract_keywords(text):
    sw = pd.read_excel('C:/Python Data/불용어.xlsx')
    stopwords = sw['불용어'].values.tolist()

    model = BertModel.from_pretrained('skt/kobert-base-v1')
    kw_model = KeyBERT(model)
    kiwi = Kiwi()
    result_text = ''
    for sentence in kiwi.analyze(text):
        nouns = []
        for token in sentence[0]:
            if token.tag.startswith('NN'):
                nouns.append(token.form)
                nouns = list(set(nouns))
        if nouns:
            result_text = ' '.join(nouns)
    print(result_text)

    keywords = kw_model.extract_keywords(result_text, keyphrase_ngram_range=(1, 1), stop_words=stopwords, top_n=20)
    return keywords