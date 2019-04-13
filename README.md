# CliamtLabServiceAndroid


Service application for employees


## API laclimat
Аторизация http://laclimat.averin.pro/api/login.php?login=[login]&password=[password]

Заявки http://laclimat.averin.pro/api/requests.php?token=[token]

Оборудование http://laclimat.averin.pro/api/equipments.php?token=[token]

Клиенты http://laclimat.averin.pro/api/clients.php?token=[token]

Изменение статуса заявки https://crm-laclimat.ru/api/request.php?token=[token]
В параметрах отправляем:
/*
 * token = Актуальный токен системы
 * acceptRequest = 1 если изменение статуса заявки на выполнено
 *   date_update = Дата изменения
 *   requestId = ID заявки
 * Успешно выполение ответ JSON result
 * В случае ошибки JSON error
  
 * cancelRequest = 1 если изменение статуса заявки на выполнено
 *   date_update = Дата изменения
 *   requestId = ID заявки
 *   comment = Комментарий мастера причина отмены заявки
 * Успешно выполение ответ JSON result
 * В случае ошибки JSON error
 */

Звершение завки https://crm-laclimat.ru/api/request.php?token=[token]
/*
 * token = Актуальный токен системы
 *
 * acceptRequest = 1 если изменение статуса заявки на выполнено
 * dateTime = Дата изменения
 * requestId = ID заявки
 * Успешно выполение ответ JSON result
 * В случае ошибки JSON error
 *
 * cancelRequest = 1 если изменение статуса заявки на выполнено
 * dateTime = Дата изменения
 * requestId = ID заявки
 * comment = Комментарий мастера причина отмены заявки
 * Успешно выполение ответ JSON result
 * В случае ошибки JSON error
 *
 * updateRequest = 1 если изменение статуса заявки на выполнено
 * Список переменных:
 * dateTime = Дата изменения
 * requestId = Int ID заявки
 * boilerModel : String
 * boilerBrand : String
 * serialNumber : String
 * presenceOfPickup : String
 * grounding : bool
 * stabilizer : bool
 * dielectricCoupling : bool
 * inletGasPressure : String
 * minimumGasOnTheBoiler : String
 * maximumGasOnTheBoiler : String
 * co : String
 * co2 : String
 * recommendations : String
 * amountToPay : String
 * amountForTheRoad : String
 * amountOfParts : String
 * boilerPhoto: Base64String
 * workResultPhoto: Base64String
 * requestType: String
 * Успешно выполение ответ JSON result
 * В случае ошибки JSON error
 *
 *
 * Дополнительный параметры
 * debug = 1 включает вывод на экран всех переданных данных
 */
