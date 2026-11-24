import { AlertTriangle, ShieldCheck, ArrowRight, X } from 'lucide-react';
import { useCart } from '../../context/CartContext';

export default function HealthAdvisoryModal() {
  const { guardrailWarning, dismissGuardrail, proceedWithWarning, addToCart } = useCart();

  if (!guardrailWarning) return null;

  const { foodItem, warningReasons, suggestedAlternatives } = guardrailWarning;

  const handleSwap = (altItem) => {
    dismissGuardrail();
    addToCart(altItem, 1, [], true);
  };

  return (
    <div className="modal-backdrop">
      <div className="modal-container health-advisory-modal">
        {/* Header */}
        <div className="modal-header-advisory">
          <div className="advisory-icon-wrap">
            <AlertTriangle size={28} className="advisory-warning-icon" />
          </div>
          <div className="advisory-title-group">
            <h3>Dietary & Health Conflict Detected</h3>
            <p>Foodie Express Health Guardrail flagged the following dish:</p>
            <div className="flagged-dish-tag">{foodItem.name}</div>
          </div>
          <button className="close-btn" onClick={dismissGuardrail}>
            <X size={20} />
          </button>
        </div>

        {/* Warning Reasons */}
        <div className="advisory-body">
          <div className="advisory-reasons-card">
            <h4>Clinical Nutritional Warnings:</h4>
            <ul>
              {warningReasons.map((reason, idx) => (
                <li key={idx} className="reason-item">
                  <span className="bullet">⚠️</span>
                  <span>{reason}</span>
                </li>
              ))}
            </ul>
          </div>

          {/* Suggested Safe Alternatives */}
          {suggestedAlternatives && suggestedAlternatives.length > 0 && (
            <div className="alternatives-section">
              <div className="alternatives-header">
                <ShieldCheck size={18} className="safe-icon" />
                <h4>Recommended Safe Alternatives (From this Kitchen):</h4>
              </div>
              <p className="alternatives-subtitle">
                These dishes are clinically verified to match your conditions, daily macro targets, and allergy profile:
              </p>

              <div className="alternatives-grid">
                {suggestedAlternatives.map((alt) => (
                  <div key={alt.id} className="alternative-card">
                    {alt.imageUrl && (
                      <img src={alt.imageUrl} alt={alt.name} className="alt-img" />
                    )}
                    <div className="alt-info">
                      <div className="alt-name">{alt.name}</div>
                      <div className="alt-meta">
                        <span className="alt-price">₹{alt.discountPrice || alt.price}</span>
                        {alt.nutritionalInfo && (
                          <span className="alt-macros">
                            {alt.nutritionalInfo.calories} kcal • {alt.nutritionalInfo.proteinGrams}g Protein • {alt.nutritionalInfo.sugarGrams}g Sugar
                          </span>
                        )}
                      </div>
                      <button className="alt-swap-btn" onClick={() => handleSwap(alt)}>
                        Swap with this dish <ArrowRight size={14} />
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>

        {/* Modal Actions */}
        <div className="modal-actions-advisory">
          <button className="btn-secondary" onClick={dismissGuardrail}>
            Cancel & Keep Browsing
          </button>
          <button className="btn-bypass" onClick={proceedWithWarning}>
            I Understand the Risk, Add Anyway
          </button>
        </div>
      </div>
    </div>
  );
}