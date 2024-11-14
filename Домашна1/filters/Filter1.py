from Домашна1.filters.Filter import *

class CodeDownloaderFilter(Filter):
    def process(self, date):
        print("Filter 1 starting...")

        url = 'https://www.mse.mk/mk/stats/symbolhistory/kmb'
        html = get_response(url)

        dropdown = html.select_one('#Code')
        options_list = dropdown.select('option')
        for option in options_list:
            if not re.search(r'\d', option.text):
                date.append(option.text)

        return date
