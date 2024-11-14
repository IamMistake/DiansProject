from Домашна1.filters.Filter import *

class DataCompletenessFilter(Filter):
    def process(self, dates):
        print("Filter 3 starting...")

        cpu_cores = os.cpu_count()
        data_chunks = split_dict(dates, cpu_cores)

        with concurrent.futures.ProcessPoolExecutor(max_workers=cpu_cores) as thread_executor:
            thread_executor.map(self.process_missing_data, data_chunks)

        return dates

    def process_missing_data(self, dates):
        now = datetime.now()
        for k, v in dates.items():
            last_date = transform_string_to_date(v)
            if last_date.date() == now.date():
                continue

            i = now - last_date
            i = int(ceil(i.days / 364))
            new_rows = self.get_new_data(k, last_date, i)
            if new_rows is None:
                continue

            output_dir = os.path.join('.', 'database', f'{k}.csv')
            read = pd.read_csv(output_dir)
            combined = pd.concat([new_rows, read], ignore_index=True) # df e prvo pa posle read
            combined.to_csv(output_dir, index=False)

    def get_new_data(self, company, last_date, years):
        tmp = {
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
        res_df = pd.DataFrame(tmp)
        session = requests.Session()
        for i in range(years):
            url = self.get_url(company, i, last_date)
            # print(url)
            soup = get_response_with_session(url, session)
            tmp_df = get_soup_df(soup)

            if tmp_df is None:
                return res_df

            res_df.join(tmp_df)

        return res_df

    def get_url(self, company, i, last_date):
        url = 'https://www.mse.mk/mk/stats/symbolhistory/' + company.lower()
        days = 364

        now = datetime.now()
        denovi = days * i
        ToDate = now - timedelta(days=denovi)
        FromDate = ToDate - timedelta(days=days)

        if FromDate < last_date:
            FromDate = last_date + timedelta(days=1)

        url += f'?FromDate={transform_date_to_string(FromDate)}&ToDate={transform_date_to_string(ToDate)}&Code={company}'

        return url
