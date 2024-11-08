from Домашна1.filters.Filter1 import *
from Домашна1.filters.Filter2 import *
from Домашна1.filters.Filter3 import *

class ScrapingPipeline:
    def __init__(self):
        self.filters = []
        self.filters.append(CodeDownloaderFilter())
        self.filters.append(SaveDataFilter())
        self.filters.append(DataCompletenessFilter())

    def add_filter(self, filter):
        self.filters.append(filter)

    def execute(self, data):
        for filter in self.filters:
            data = filter.process(data)
        return data