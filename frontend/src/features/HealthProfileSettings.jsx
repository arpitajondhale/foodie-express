import { useState, useEffect } from 'react';
import { useHealthProfile } from '../context/HealthProfileContext';
import {
  ShieldCheck,
  HeartPulse,
  AlertCircle,
  Save,
  CheckCircle2,
  Activity,
  Flame,
} from 'lucide-react';

export default function HealthProfileSettings() {
  const { profile, updateProfile, loading } = useHealthProfile();

  const [conditions, setConditions] = useState([]);
  const [allergies, setAllergies] = useState([]);
  const [dietPreference, setDietPreference] = useState('VEG');
  const [dailyCalorieTarget, setDailyCalorieTarget] = useState(2000);
  const [maxSugar, setMaxSugar] = useState(15.0);
  const [maxSodium, setMaxSodium] = useState(600.0);
  const [savedSuccess, setSavedSuccess] = useState(false);

  useEffect(() => {
    if (profile) {
      setConditions(profile.conditions || []);
      setAllergies(profile.allergies || []);
      setDietPreference(profile.dietPreference || 'VEG');
      setDailyCalorieTarget(profile.dailyCalorieTarget || 2000);
      setMaxSugar(profile.maxSugarGramsPerMeal || 15.0);
      setMaxSodium(profile.maxSodiumMgPerMeal || 600.0);
    }
  }, [profile]);

  const conditionOptions = [
    { key: 'DIABETES', label: 'Type 2 / Pre-Diabetes', desc: 'Alerts on >12g sugar, high glycemic index & rapid spikes' },
    { key: 'HYPERTENSION', label: 'Hypertension (High BP)', desc: 'Caps meal sodium to 500mg, warns on heavy salt' },
    { key: 'HEART_DISEASE', label: 'Heart & Cardiovascular', desc: 'Restricts saturated trans-fats and excessive sodium' },
    { key: 'PCOS', label: 'PCOS / PCOD', desc: 'Reduces insulin-resistance triggers and refined carbs' },
    { key: 'CELIAC_GLUTEN_INTOLERANCE', label: 'Celiac Disease / Gluten Free', desc: 'Strict auto-rejection of wheat, barley, maida' },
    { key: 'LACTOSE_INTOLERANCE', label: 'Lactose Intolerance', desc: 'Flags milk, cream, paneer, and dairy derivatives' },
    { key: 'KIDNEY_FRIENDLY', label: 'Kidney-Friendly (Low Renal Load)', desc: 'Limits excessive potassium, sodium and protein load' },
  ];

  const allergyOptions = [
    { key: 'NUTS', label: 'Peanuts & Tree Nuts' },
    { key: 'DAIRY', label: 'Dairy / Milk' },
    { key: 'SOY', label: 'Soybeans & Tofu' },
    { key: 'GLUTEN', label: 'Gluten / Wheat' },
    { key: 'EGGS', label: 'Eggs' },
    { key: 'SHELLFISH', label: 'Shellfish / Seafood' },
  ];

  const dietOptions = [
    { key: 'VEG', label: 'Vegetarian' },
    { key: 'NON_VEG', label: 'Non-Vegetarian' },
    { key: 'VEGAN', label: 'Vegan (100% Plant Based)' },
    { key: 'EGGETARIAN', label: 'Eggetarian' },
    { key: 'JAIN', label: 'Jain (No Root Veg)' },
    { key: 'KETO', label: 'Keto (Low Carb High Fat)' },
  ];

  const toggleCondition = (key) => {
    setConditions((prev) =>
      prev.includes(key) ? prev.filter((k) => k !== key) : [...prev, key]
    );
  };

  const toggleAllergy = (key) => {
    setAllergies((prev) =>
      prev.includes(key) ? prev.filter((k) => k !== key) : [...prev, key]
    );
  };

  const handleSave = async (e) => {
    e.preventDefault();
    await updateProfile({
      conditions,
      allergies,
      dietPreference,
      dailyCalorieTarget: Number(dailyCalorieTarget),
      maxSugarGramsPerMeal: Number(maxSugar),
      maxSodiumMgPerMeal: Number(maxSodium),
    });
    setSavedSuccess(true);
    setTimeout(() => setSavedSuccess(false), 3000);
  };

  return (
    <div className="health-profile-page">
      <div className="health-profile-container">
        {/* Header */}
        <div className="profile-banner">
          <div className="banner-icon-box">
            <HeartPulse size={32} />
          </div>
          <div>
            <h2>Clinical Health & Dietary Guardrail Profile</h2>
            <p>
              Your diagnosed conditions, food allergies, and nutritional limits are continuously evaluated
              by our automated clinical engine whenever you browse or add dishes to your cart.
            </p>
          </div>
        </div>

        {savedSuccess && (
          <div className="success-banner">
            <CheckCircle2 size={18} />
            <span>Health profile saved and synchronized with menu guardrails!</span>
          </div>
        )}

        <form onSubmit={handleSave} className="health-form">
          {/* 1. Medical Conditions */}
          <div className="form-card">
            <div className="card-header-row">
              <Activity size={20} className="text-primary" />
              <div>
                <h3>1. Diagnosed Medical Conditions</h3>
                <span className="card-sub">Select any conditions you manage to enable automated warnings:</span>
              </div>
            </div>

            <div className="conditions-grid">
              {conditionOptions.map((cond) => {
                const active = conditions.includes(cond.key);
                return (
                  <div
                    key={cond.key}
                    className={`condition-select-card ${active ? 'active' : ''}`}
                    onClick={() => toggleCondition(cond.key)}
                  >
                    <div className="checkbox-round">{active && <CheckCircle2 size={16} />}</div>
                    <div className="cond-text">
                      <div className="cond-label">{cond.label}</div>
                      <div className="cond-desc">{cond.desc}</div>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>

          {/* 2. Food Allergies */}
          <div className="form-card">
            <div className="card-header-row">
              <AlertCircle size={20} className="text-orange" />
              <div>
                <h3>2. Food Allergies & Intolerances</h3>
                <span className="card-sub">Dishes containing these allergens will be flagged with warning advisories:</span>
              </div>
            </div>

            <div className="allergies-chips-wrap">
              {allergyOptions.map((all) => {
                const active = allergies.includes(all.key);
                return (
                  <button
                    key={all.key}
                    type="button"
                    className={`allergy-chip ${active ? 'active' : ''}`}
                    onClick={() => toggleAllergy(all.key)}
                  >
                    {active ? '✓ ' : '+ '} {all.label}
                  </button>
                );
              })}
            </div>
          </div>

          {/* 3. Diet Preference */}
          <div className="form-card">
            <div className="card-header-row">
              <ShieldCheck size={20} className="text-success" />
              <div>
                <h3>3. Diet Preference</h3>
                <span className="card-sub">Choose your default lifestyle preference:</span>
              </div>
            </div>

            <div className="diet-options-grid">
              {dietOptions.map((d) => (
                <div
                  key={d.key}
                  className={`diet-option-box ${dietPreference === d.key ? 'active' : ''}`}
                  onClick={() => setDietPreference(d.key)}
                >
                  <div className="radio-dot" />
                  <span>{d.label}</span>
                </div>
              ))}
            </div>
          </div>

          {/* 4. Meal Macro Targets */}
          <div className="form-card">
            <div className="card-header-row">
              <Flame size={20} className="text-red" />
              <div>
                <h3>4. Safe Macro Thresholds Per Meal</h3>
                <span className="card-sub">Customise your meal-level nutritional safety ceiling:</span>
              </div>
            </div>

            <div className="macro-inputs-grid">
              <div className="macro-field">
                <label>Max Sugar Per Meal (grams)</label>
                <div className="input-with-unit">
                  <input
                    type="number"
                    min="1"
                    max="60"
                    step="0.5"
                    value={maxSugar}
                    onChange={(e) => setMaxSugar(e.target.value)}
                  />
                  <span className="unit">g</span>
                </div>
                <span className="field-hint">Diabetic limit recommended: 10-15g</span>
              </div>

              <div className="macro-field">
                <label>Max Sodium Per Meal (milligrams)</label>
                <div className="input-with-unit">
                  <input
                    type="number"
                    min="100"
                    max="2000"
                    step="25"
                    value={maxSodium}
                    onChange={(e) => setMaxSodium(e.target.value)}
                  />
                  <span className="unit">mg</span>
                </div>
                <span className="field-hint">Hypertension limit recommended: 400-600mg</span>
              </div>

              <div className="macro-field">
                <label>Daily Calorie Target</label>
                <div className="input-with-unit">
                  <input
                    type="number"
                    min="1000"
                    max="4000"
                    step="50"
                    value={dailyCalorieTarget}
                    onChange={(e) => setDailyCalorieTarget(e.target.value)}
                  />
                  <span className="unit">kcal</span>
                </div>
                <span className="field-hint">Daily nutritional reference intake</span>
              </div>
            </div>
          </div>

          {/* Submit */}
          <div className="form-actions">
            <button type="submit" className="save-profile-btn" disabled={loading}>
              <Save size={18} /> {loading ? 'Saving Profile...' : 'Save & Sync Health Guardrails'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}