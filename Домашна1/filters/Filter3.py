from Домашна1.filters.Filter import *

class DataCompletenessFilter(Filter):
    def process(self, dates):
        print("Filter 3 starting...")

        now = datetime.now()
        for k, v in dates.items():
            last_date = transform_string_to_date(v)
            if last_date.date() == now.date():
                continue

            i = now - last_date
            i = int(ceil(i.days / 364))
            df = self.get_new_data(k, last_date, i)
            if df is None:
                continue

            output_dir = os.path.join('.', 'database', f'{k}.xlsx')
            read = pd.read_excel(output_dir)
            combined = pd.concat([df, read], ignore_index=True) # df e prvo pa posle read
            combined.to_excel(output_dir, index=False)
        return dates

    def get_new_data(self, company, last_date, years):
        for i in range(years):
            url = self.get_url(company, i, last_date)
            # print(url)
            soup = get_response(url)
            res = get_soup_df(soup)
        return res

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