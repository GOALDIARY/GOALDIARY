import numpy as np
from sentence_transformers import SentenceTransformer

class text_embedding():
  def __init__(self, model_name):
    self.model_name = model_name

  def fit(self, X, y=None):
    return self

  def transform(self, X, record_type):
    embedding_model = SentenceTransformer(self.model_name)
    embedding_vec = embedding_model.encode(X['발화문'])
    if record_type == 0:
        X_val = np.concatenate((X.drop(['final_label', '발화문'], axis = 1), embedding_vec), axis = 1)
    elif record_type == 1:
        X_val = np.concatenate((X.drop(['wav_id', 'final_label', '발화문'], axis = 1), embedding_vec), axis = 1)
    return X_val