import numpy as np

def emotion_result(result):
    emotion_dict = {'공포': round(result[0][0]*100,2), '분노': round(result[0][1]*100,2),
                    '슬픔': round(result[0][2]*100,2), '중립': round(result[0][3]*100,2),
                    '행복': round(result[0][4]*100,2), '혐오': round(result[0][5]*100,2)}
    final_emotion = ''

    if np.argmax(result) == 0:
        final_emotion = '공포'
    elif np.argmax(result) == 1:
        final_emotion = '분노'
    elif np.argmax(result) == 2:
        final_emotion = '슬픔'
    elif np.argmax(result) == 3:
        final_emotion = '중립'
    elif np.argmax(result) == 4:
        final_emotion = '행복'
    elif np.argmax(result) == 5:
        final_emotion = '혐오'

    print(emotion_dict)
    print(f'최종 감정: {final_emotion}')

    return emotion_dict, final_emotion