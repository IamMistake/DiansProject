import concurrent.futures
import os.path

from Домашна1.filters.Filter import *

cpu_cores = os.cpu_count()
num_threads = 4

class SaveDataFilter(Filter):
    def process(self, data):
        print("Filter 2 starting...")

        # cpu_cores = os.cpu_count()
        # num_threads = cpu_cores * 2
        print("Cores: ", cpu_cores)
        print("Threads: ", num_threads)
        chunk_size = ceil(len(data) / num_threads)
        data_chunks = [data[i:i + chunk_size] for i in range(0, len(data), chunk_size)]

        path = os.path.join('.', 'database')
        if not os.path.exists(path):
            os.mkdir(path)

        all_dates = {}
        with concurrent.futures.ProcessPoolExecutor(max_workers=cpu_cores) as executor:
            tmp = list(executor.map(self.process_companies_subset, data_chunks))
            for result in tmp:
                all_dates.update(result)

        # print(all_dates)
        return all_dates

    def process_task(self, data_chunk):
        vrati = {}
        with concurrent.futures.ThreadPoolExecutor(max_workers=num_threads) as thread_executor:
            chunk_size = ceil(len(data_chunk) / num_threads)
            data_chunks = [data_chunk[i:i + chunk_size] for i in range(0, len(data_chunk), chunk_size)]

            results = list(thread_executor.map(self.process_companies_subset, data_chunks))
            for result in results:
                vrati.update(result)
        return vrati

    def process_companies_subset(self, companies_subset):
        dates = {}

        # print("Company_subset: ", companies_subset)
        for company in companies_subset:
            if check_existing_data(company):
                dates[company] = self.get_last_date(company)
            else:
                dates[company] = transform_date_to_string(datetime.now())
                self.save_last_10_years_csv(company)
                # self.save_last_10_years(company)
        return dates

    def get_last_date(self, company_name):
        path = os.path.join('.', 'database', f'{company_name}.csv')
        with open(path, mode="r", newline='') as file:
            csv_reader = csv.reader(file)
            next(csv_reader)
            first_line = next(csv_reader)
            return first_line[0]
        # df = pd.read_csv(path)
        # return df.iloc[0, 0]

    def save_last_10_years_csv(self, company_name):
        output_dir = os.path.join('.', 'database', f'{company_name}.csv')
        csv_data = [
            ["Date", "Price of last transaction", "Max.", "Min.", "Average price", "%prom.", "Quantity", "BEST turnover in denars", "Total turnover in denars"]
        ]
        session = requests.Session()

        for i in range(10):
            # print(i, company_name)

            url = self.get_url(company_name, i)
            soup = get_response_with_session(url, session)
            tmp_csv = get_soup_csv(soup)

            if tmp_csv is None:
                # print("Found None in " + str(i) + " " + company_name)
                break

            csv_data += tmp_csv

        save_csv(output_dir, csv_data)

    def get_url(self, company, i):
        url = 'https://www.mse.mk/mk/stats/symbolhistory/' + company.lower()
        days = 364

        now = datetime.now()
        denovi = days * i
        ToDate = now - timedelta(days=denovi)
        FromDate = ToDate - timedelta(days=days)

        url += f'?FromDate={transform_date_to_string(FromDate)}&ToDate={transform_date_to_string(ToDate)}&Code={company}'

        return url
