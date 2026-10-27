package academy.tochkavhoda.competition.exception;

public enum ErrorCode {
    WRONG_JSON("Некорректный json запроса"),
    EMPTY_FIRST_NAME("Имя не может быть пустым"),
    EMPTY_LAST_NAME("Фамилия не может быть пустой"),
    EMPTY_COMPANY_NAME("Название фирмы не может быть пустым"),
    WRONG_LOGIN("Логин должен содержать не менее 4 символов: латинские буквы, цифры, '_'"),
    WRONG_PASSWORD("Пароль должен содержать не менее 8 символов, хотя бы одну цифру и один спецсимвол"),
    EMPTY_DIRECTIONS("Список направлений не может быть пустым"),
    WRONG_DIRECTION("Название направления не может быть пустым"),
    EMPTY_TITLE("Название заявки не может быть пустым"),
    EMPTY_DESCRIPTION("Описание заявки не может быть пустым"),
    WRONG_AMOUNT("Сумма должна быть положительной"),
    WRONG_RATING("Оценка должна быть в диапазоне 1..5"),
    WRONG_THRESHOLD("Пороговое значение должно быть в диапазоне 0..5"),
    LOGIN_ALREADY_USED("Этот логин уже используется"),
    WRONG_LOGIN_OR_PASSWORD("Неверный логин или пароль"),
    WRONG_TOKEN("Недействительный токен"),
    NOT_PARTICIPANT("Операция доступна только участнику"),
    NOT_EXPERT("Операция доступна только эксперту"),
    APPLICATION_NOT_FOUND("Заявка не найдена"),
    NOT_OWN_APPLICATION("Можно отменить только свою заявку"),
    DIRECTION_NOT_OF_EXPERT("Эксперт не является специалистом по направлению"),
    RATING_ALREADY_EXISTS("Оценка этой заявке уже поставлена"),
    RATING_NOT_FOUND("Оценка не найдена"),
    COMPETITION_FINISHED("Конкурс завершён, операция невозможна");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
