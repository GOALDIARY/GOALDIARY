import difflib

def SequenceMatcher(text1, text2):
    answer_bytes = bytes(text1, 'utf-8')
    input_bytes = bytes(text2, 'utf-8')
    answer_bytes_list = list(answer_bytes)
    input_bytes_list = list(input_bytes)
    return difflib.SequenceMatcher(None, answer_bytes_list, input_bytes_list).ratio()