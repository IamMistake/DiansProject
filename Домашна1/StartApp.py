from Домашна1.filters.Filter1 import *
from Домашна1.pipelines.PipeLine import *

if __name__ == '__main__':

    print("ScrapingPipeline starting...")
    timer1 = time.time()
    url = 'https://www.mse.mk/mk/stats/symbolhistory/kmb'

    pipeline = ScrapingPipeline()
    filtered_data = pipeline.execute([])

    duration = time.time() - timer1
    print("Whole app finished in {:.2f} seconds".format(duration))
    print("ScrapingPipeline finished!!!")