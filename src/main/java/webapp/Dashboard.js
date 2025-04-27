import React, { useState } from "react";
import './Dashboard.css';
import { LineChart, Line, CartesianGrid, XAxis, YAxis, Tooltip, ReferenceLine } from "recharts";

function Dashboard() {
    const [billData, setBillData] = useState([]);
    const [suggestionsVisible, setSuggestionsVisible] = useState(false);
    const [threshold, setThreshold] = useState('');
    const [savedThreshold, setSavedThreshold] = useState(null);
    const [showThresholdPopup, setShowThresholdPopup] = useState(true);

    const handleSetThreshold = () => {
        if (threshold) {
            setSavedThreshold(Number(threshold));
            setShowThresholdPopup(false);
        }
    };

    const handleUpdate = () => {
        setBillData(prevData => {
            if (prevData.length >= 30) {
                setSuggestionsVisible(false);
                return [];
            }

            const lastTotal = prevData.length > 0 ? prevData[prevData.length - 1].total : 0;
            const newAmount = Math.floor(Math.random() * 26) + 25; // 25-50 arası
            const newTotal = lastTotal + newAmount;
            const updatedData = [...prevData, { day: prevData.length + 1, total: newTotal }];

            const regression = getRegressionLine(updatedData);
            const day30 = regression.find(p => p.day === 30);

            if (day30 && savedThreshold !== null) {
                if (day30.regression >= savedThreshold) {
                    setSuggestionsVisible(true);
                } else {
                    setSuggestionsVisible(false);
                }
            } else {
                setSuggestionsVisible(false);
            }

            return updatedData;
        });
    };


    const getRegressionLine = (data) => {
        if (data.length < 1) return [];

        const n = data.length;
        const sumX = data.reduce((acc, point) => acc + point.day, 0);
        const sumY = data.reduce((acc, point) => acc + point.total, 0);
        const sumXY = data.reduce((acc, point) => acc + point.day * point.total, 0);
        const sumX2 = data.reduce((acc, point) => acc + point.day * point.day, 0);

        const slope = n > 1 ? (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX) : 0;
        const intercept = n > 1 ? (sumY - slope * sumX) / n : sumY / (sumX || 1);

        return Array.from({ length: 30 }, (_, i) => ({
            day: i + 1,
            regression: slope * (i + 1) + intercept,
        }));
    };

    const regressionData = getRegressionLine(billData);

    const mergedData = Array.from({ length: 30 }, (_, i) => ({
        day: i + 1,
        total: billData[i]?.total ?? null,
        regression: regressionData[i]?.regression ?? null,
    }));

    return (
        <div className="container">
            {showThresholdPopup && (
                <div className="popup-overlay">
                    <div className="popup">
                        <h2>Enter Threshold</h2>
                        <input
                            type="number"
                            placeholder="Enter threshold (e.g., 1000)"
                            value={threshold}
                            onChange={(e) => setThreshold(e.target.value)}
                            className="threshold-input"
                        />
                        <button onClick={handleSetThreshold} className="set-threshold-button">Set</button>
                    </div>
                </div>
            )}

            {!showThresholdPopup && (
                <>
                    <div className="graphSection">
                        <div className="graphBox">
                            <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                                <LineChart
                                    width={800}
                                    height={400}
                                    data={mergedData.length > 0 ? mergedData : Array.from({ length: 30 }, (_, i) => ({ day: i + 1, total: 0 }))}
                                    margin={{ top: 20, right: 20, bottom: 20, left: 20 }}
                                >
                                    <XAxis type="number" dataKey="day" domain={[1, 30]} tickCount={30} interval={0} tick={{ fontSize: 14, fill: '#333' }} />
                                    <YAxis domain={[0, Math.max(1000, savedThreshold)]} tick={{ fontSize: 14, fill: '#333' }} />
                                    <CartesianGrid stroke="#ccc" />
                                    <Tooltip />
                                    {savedThreshold !== null && (
                                        <ReferenceLine
                                            y={savedThreshold}
                                            stroke="red"
                                            strokeDasharray="5 5"
                                            label={{
                                                value: `Threshold (${savedThreshold} kWh)`,
                                                position: 'top',
                                                fill: 'red',
                                                fontSize: 12,
                                                fontWeight: 'bold'
                                            }}
                                        />
                                    )}
                                    <Line type="monotone" dataKey="total" stroke="#8884d8" name="Bill Amount" connectNulls={false} />
                                    {billData.length > 1 && (
                                        <Line type="monotone" dataKey="regression" stroke="#82ca9d" name="Trend Line" dot={false} />
                                    )}
                                </LineChart>


                                <button className="updateButton" onClick={handleUpdate} style={{ marginTop: '20px' }}>
                                    Update
                                </button>
                            </div>
                        </div>
                    </div>

                    <div className="suggestionsSection">
                        <h3>Suggestions</h3>
                        <div className="suggestionsTableContainer">
                            <table className="suggestionsTable">
                                <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Suggestion</th>
                                </tr>
                                </thead>
                                <tbody>
                                {suggestionsVisible ? (
                                    <>
                                        <tr><td>1</td><td>Optimize usage: Turn off devices when not in use to save energy.</td></tr>
                                        <tr><td>2</td><td>Use smart devices: Automate energy savings with smart plugs and thermostats.</td></tr>
                                        <tr><td>3</td><td>Turn off standby devices: Unplug devices that consume standby power.</td></tr>
                                        <tr><td>4</td><td>Switch to LED lights: Use LEDs to cut lighting costs dramatically.</td></tr>
                                        <tr><td>5</td><td>Adjust thermostat settings: Fine-tune heating and cooling for efficiency.</td></tr>
                                        <tr><td>6</td><td>Install solar panels: Reduce dependency on the grid by producing your own power.</td></tr>
                                        <tr><td>7</td><td>Use energy-efficient appliances: Replace old devices with energy-star rated ones.</td></tr>
                                        <tr><td>8</td><td>Insulate doors and windows: Keep your home's temperature stable and lower HVAC needs.</td></tr>
                                        <tr><td>9</td><td>Schedule HVAC maintenance: Ensure systems work efficiently and save energy.</td></tr>
                                        <tr><td>10</td><td>Use motion sensor lights: Lights turn on only when needed, saving energy automatically.</td></tr>
                                    </>
                                ) : (
                                    <tr>
                                        <td colSpan="2" style={{ textAlign: "center", color: "#aaa" }}>
                                            No suggestions yet.
                                        </td>
                                    </tr>
                                )}
                                </tbody>
                            </table>
                        </div>
                    </div>
                </>
            )}
        </div>
    );
}

export default Dashboard;