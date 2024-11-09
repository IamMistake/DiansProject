import os.path

from filters.Filter import *

class SaveDataFilter(Filter):
    def process(self, data):
        print("Filter 2 starting...")

        cpu_cores = os.cpu_count()

        num_threads = cpu_cores * 2
        print("Threads: ", num_threads)
        chunk_size = ceil(len(data) / num_threads)
        data_chunks = [data[i:i + chunk_size] for i in range(0, len(data), chunk_size)]

        path = os.path.join('.', 'database')
        if not os.path.exists(path):
            os.mkdir(path)

        all_dates = {}
        with concurrent.futures.ThreadPoolExecutor(max_workers=num_threads) as executor:
            # Submit each data chunk to a separate thread
            futures = [executor.submit(self.process_companies_subset, chunk) for chunk in data_chunks]

            # Collect results as they complete
            for future in concurrent.futures.as_completed(futures):
                all_dates.update(future.result())
                # print(all_dates)

        return all_dates

    def process_companies_subset(self, companies_subset):
        dates = {}

        # print(companies_subset)
        for company in companies_subset:
            if check_existing_data(company):
                dates[company] = self.get_last_date(company)
            else:
                dates[company] = transform_date_to_string(datetime.now())
                self.save_last_10_years(company)
        return dates

    def get_last_date(self, company_name):
        path = os.path.join('.', 'database', f'{company_name}.xlsx')
        df = pd.read_excel(path)
        return df.iloc[0, 0]

    def save_last_10_years(self, company_name):
        output_dir = os.path.join('.', 'database', f'{company_name}.xlsx')
        tmp = {
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
        excelce = pd.DataFrame(tmp)
        for i in range(10):
            # print(i, company_name)

            url = self.get_url(company_name, i)
            soup = get_response(url)
            df = get_soup_df(soup)

            if df is None:
                # print("Found None in " + str(i) + " " + company_name)
                break

            excelce = pd.concat([excelce, df], ignore_index=True)

        excelce.to_excel(output_dir, index=False)

    def get_url(self, company, i):
        url = 'https://www.mse.mk/mk/stats/symbolhistory/' + company.lower()
        days = 364

        now = datetime.now()
        denovi = days * i
        ToDate = now - timedelta(days=denovi)
        FromDate = ToDate - timedelta(days=days)

        url += f'?FromDate={transform_date_to_string(FromDate)}&ToDate={transform_date_to_string(ToDate)}&Code={company}'

        return url
