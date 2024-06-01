from openai import OpenAI
client = OpenAI(api_key = 'open api key')

def get_feedback(txt):
    response = client.chat.completions.create(
    model="gpt-3.5-turbo",
    messages=[
        {
            "role": "system",
            "content": txt
        }
    ]
    )
    feedback = response.choices[0].message.content.replace('\n\n', ' ')
    return feedback