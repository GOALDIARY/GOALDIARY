import numpy as np
import librosa

def extract_features(file_name):
    
    audio_signal, sample_rate = librosa.load(file_name, sr=22050)
    
    #spectrogram 구현
    spectrogram = librosa.stft(audio_signal, n_fft=512)
    spectrogram = np.abs(spectrogram)
    
    #Mel-spectrogram 구현
    power_spectrogram = spectrogram**2
    mel = librosa.feature.melspectrogram(S=power_spectrogram, sr=sample_rate)
    mel = librosa.power_to_db(mel)
    
    #mfcc 구현
    mfccs = librosa.feature.mfcc(S = mel, n_mfcc=20)
    mfcc_feature = np.mean(mfccs.T, axis=0)
    
    return mfcc_feature