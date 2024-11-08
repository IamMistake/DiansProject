from math import ceil
import pandas as pd
import requests
from bs4 import BeautifulSoup
import concurrent.futures
import time
from datetime import datetime, timedelta
import os
from datetime import datetime
import re

def transform_string_to_date(stringce):
    date_format = "%d.%m.%Y"
    return datetime.strptime(stringce, date_format)


def transform_date_to_string(date):
    tmp = date.__str__().split(' ')[0].split("-")
    return tmp[2] + "." + tmp[1] + "." + tmp[0]


def check_existing_data(company_name):
    path = os.path.join('.', 'database', f'{company_name}.xlsx')
    if os.path.exists(path):
        return True
    return False

def get_soup_df(soup):
    table = soup.select_one('#resultsTable')
    if table is None:
        return None
    trs = table.select('tbody > tr')

    df = {
        "Датум": [],
        "Цена на последна трансакција": [],
        "Мак.": [],
        "Мин.": [],
        "Просечна цена": [],
        "%пром.": [],
        "Количина": [],
        "Промет во БЕСТ во денари": [],
        "Вкупен промет во денари": [],
    }

    for tr in trs:
        tds = tr.select('td')

        df['Датум'].append(tds[0].text)
        df['Цена на последна трансакција'].append(tds[1].text)
        df['Мак.'].append(tds[2].text)
        df['Мин.'].append(tds[3].text)
        df['Просечна цена'].append(tds[4].text)
        df['%пром.'].append(tds[5].text)
        df['Количина'].append(tds[6].text)
        df['Промет во БЕСТ во денари'].append(tds[7].text)
        df['Вкупен промет во денари'].append(tds[8].text)

    tmp = pd.DataFrame(df)
    return tmp

def get_response(url):
    response = requests.get(url)
    return BeautifulSoup(response.text, 'html.parser')

class Filter:
    def process(self, data):
        raise NotImplementedError("Each filter must implement!")
