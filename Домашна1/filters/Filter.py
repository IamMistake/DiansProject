from math import ceil
import pandas as pd
import csv
import requests
from bs4 import BeautifulSoup
from multiprocessing import Pool, Manager
import concurrent.futures
import time
from datetime import datetime, timedelta
import os
from datetime import datetime
import re
import openpyxl

def transform_string_to_date(stringce):
    date_format = "%d.%m.%Y"
    return datetime.strptime(stringce, date_format)


def transform_date_to_string(date):
    tmp = date.__str__().split(' ')[0].split("-")
    return tmp[2] + "." + tmp[1] + "." + tmp[0]


def check_existing_data(company_name):
    path = os.path.join('.', 'database', f'{company_name}.csv')
    if os.path.exists(path):
        return True
    return False

def save_csv(output_dir, data):
    with open(output_dir, 'w', newline='') as file:
        writer = csv.writer(file)
        writer.writerows(data)

def get_soup_csv(soup):
    table = soup.select_one('#resultsTable')
    if table is None:
        return None
    trs = table.select('tbody > tr')

    csv_data = []
    for tr in trs:
        tds = tr.select('td')
        tmpce = []
        for td in tds:
            tmpce.append(td.text)
        csv_data.append(tmpce)

    return csv_data

def get_soup_df(soup):
    table = soup.select_one('#resultsTable')
    if table is None:
        return None
    trs = table.select('tbody > tr')

    df = {
        "Date": [],
        "Price of last transaction": [],
        "Max.": [],
        "Min.": [],
        "Average price": [],
        "%prom.": [],
        "Quantity": [],
        "BEST turnover in denars": [],
        "Total turnover in denars": [],
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

def get_response_with_session(url, session):
    response = session.get(url)
    return BeautifulSoup(response.text, 'html.parser')


def split_dict(input_dict, n):
    items = list(input_dict.items())
    split_size = len(items) // n
    remainder = len(items) % n

    splits = []
    start = 0
    for i in range(n):
        end = start + split_size + (1 if i < remainder else 0)
        splits.append(dict(items[start:end]))
        start = end

    return splits

class Filter:
    def process(self, data):
        raise NotImplementedError("Each filter must implement!")
