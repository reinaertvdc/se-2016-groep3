from pdf_parser import PdfParser


class ModularCertificateParser:
    __CERTIFICATE_NAME_BEGIN_TAG = 'De opleiding '
    __CERTIFICATE_NAME_END_TAG = 'bestaat '
    __NEWLINE_TAG = '^'
    __MODULES_LIST_START_TAG = 'modules: ' + __NEWLINE_TAG + ' ' + __NEWLINE_TAG
    __MODULES_LIST_SPLIT_TAG = ' ' + __NEWLINE_TAG
    __MODULES_LIST_END_TAG = ' ' + __NEWLINE_TAG + __NEWLINE_TAG

    def __init__(self):
        self.pdf_parser = PdfParser()
        self.content = ''

    def __parse_name(self):
        name_begin_index = self.content.find(self.__CERTIFICATE_NAME_BEGIN_TAG) + len(self.__CERTIFICATE_NAME_BEGIN_TAG)
        name_end_index = self.content.find(self.__CERTIFICATE_NAME_END_TAG, name_begin_index) - 1
        return self.content[name_begin_index:name_end_index]

    def __parse_module_names(self):
        module_names = []
        current_module_name_index = self.content.find(self.__MODULES_LIST_START_TAG) + len(
            self.__MODULES_LIST_START_TAG)
        module_names_end_index = self.content.find(self.__MODULES_LIST_END_TAG, current_module_name_index)
        while True:
            next_module_name_index = self.content.find(self.__MODULES_LIST_SPLIT_TAG, current_module_name_index) + len(
                self.__MODULES_LIST_SPLIT_TAG)
            module_name = self.content[
                          current_module_name_index:next_module_name_index - len(self.__MODULES_LIST_SPLIT_TAG)]
            if len(module_name) > 0:
                module_names.append(module_name)
            current_module_name_index = next_module_name_index
            if module_names_end_index - current_module_name_index <= 1:
                break
        return module_names

    def parse(self, path):
        self.content = ' '.join(self.pdf_parser.to_plain_text(path).replace("\n", self.__NEWLINE_TAG).split())
        name = self.__parse_name()
        module_names = self.__parse_module_names()

        print name + ' (' + path + ')'
        for module_name in module_names:
            print '    ' + module_name