package com.example.sizetracker.utils

/**
 * Калькулятор калорий по формуле Миффлина-Сан Жеора
 */
object CalorieCalculator {
    /**
     * Рассчитывает дневную норму калорий
     * @param weight вес в кг
     * @param height рост в см
     * @param age возраст в годах
     * @param gender пол ("male" или "female")
     * @param activityLevel уровень активности (по умолчанию 1.2 - минимальная активность)
     * @return дневная норма калорий
     */
    fun calculateDailyCalories(
        weight: Float,
        height: Int,
        age: Int,
        gender: String,
        activityLevel: Float = 1.2f
    ): Int {
        // Формула Миффлина-Сан Жеора
        val bmr = if (gender == "male") {
            // Для мужчин: BMR = 10 * вес(кг) + 6.25 * рост(см) - 5 * возраст(г) + 5
            10 * weight + 6.25 * height - 5 * age + 5
        } else {
            // Для женщин: BMR = 10 * вес(кг) + 6.25 * рост(см) - 5 * возраст(г) - 161
            10 * weight + 6.25 * height - 5 * age - 161
        }

        // Умножаем на коэффициент активности
        return (bmr * activityLevel).toInt()
    }
}
