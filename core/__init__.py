import gettext
from core import config

_ = gettext.translation(__package__, localedir="../lang", languages=[config.core["language"]]).install()
