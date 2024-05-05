import numpy as np
import pandas as pd
import pickle
import joblib
import speech_recognition as sr
import urllib.request
from sentence_transformers import SentenceTransformer
from text_embedding import text_embedding
from extract_features import extract_features
from emotion_result import emotion_result
from get_feedback import get_feedback

type = 0

if type == 0:
    with open('C:/Python Data/variable/text_scaler.pickle', 'rb') as f:
        text_scaler = pickle.load(f)
    text_model = joblib.load('C:/Python Data/model/text_svc_model.pkl')
    
    txt = '아침에 늦잠을 자서 헬스장에 가지를 못했네...'
    print(txt)

    d = pd.DataFrame(columns=['발화문', 'final_label'])
    d.loc[0] = [txt,'']
    t = text_embedding(model_name = 'jhgan/ko-sbert-multitask').transform(d, type)
    t_scaled = text_scaler.transform(t)
    
    result = text_model.predict_proba(t_scaled)
    emotion_dict, final_emotion = emotion_result(result)

    if final_emotion in ['공포', '분노', '슬픔', '혐오']:
        txt += ' 오늘의 감정은 ' + final_emotion + '입니다. 감정에 따른 피드백 부탁드립니다.'
        feedback = get_feedback(txt)
        print(feedback)
    elif final_emotion in ['중립', '행복']:
        print(txt)


elif type == 1:
    with open('C:/Python Data/variable/sound_scaler.pickle', 'rb') as f:
        sound_scaler = pickle.load(f)
    sound_model = joblib.load('C:/Python Data/model/sound_svc_model.pkl')

    link = 'C:/Python Data/record file/health.wav'
    r = sr.Recognizer()
    audio = sr.AudioFile(link)

    with audio as source:
        audio = r.record(source)

    txt = r.recognize_google(audio, language='ko-KR')
    print(txt)

    audio_test_feature = extract_features(link)
    audio_test_feature = np.reshape(audio_test_feature, (-1,20))
    audio_test_feature = pd.DataFrame(audio_test_feature)
    audio_test_feature['wav_id'] = ''
    audio_test_feature['final_label'] = ''
    audio_test_feature['발화문'] = txt
    a = text_embedding(model_name = 'jhgan/ko-sbert-multitask').transform(audio_test_feature, type)
    a_scaled = sound_scaler.transform(a)

    result = sound_model.predict_proba(a_scaled)
    emotion_dict, final_emotion = emotion_result(result)