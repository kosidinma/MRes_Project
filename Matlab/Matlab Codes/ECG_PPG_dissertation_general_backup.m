%code for finding data peaks by Kosy Onyenso
clear all;
load('Charence data.mat');
Sampling_rate = 1/25;  %0.05s sampling rate
%fid = fopen('ECG.txt', 'r');
A = ecg(4000:end); %ECG
A2 = ankle(4000:end); %ankle
A3 = finger(4000:end); %finger


% A = ecg(1500:end); %ECG
% A2 = ankle(1500:end); %ankle
% A3 = finger(1500:end); %finger


% A = ecg; %ECG
% A2 = ankle; %ankle
% A3 = finger; %finger
[m,~] = size(A);
[m2,~] = size(A2);
num_drops = 0;
window_no = 0;
k = 1;
k2 = 1;
k3 = 1;
for e = 1: 1 : m
%    A(e) = 8000 - A(e); %signal was upside down
%     if (A(e) > 5000)
%         A(e) = 5000;
%     end
%     if (A(e) < 4200) %baseline
%         A(e) = 4200;
%     end
end

pk_place = 0;
pk_dif = 0;
jy = 0;

for i = 6: 1 : m - 6
    if ((A3(i) > A3(i+1)) && (A3(i) > A3(i+4)) && (A3(i) > A3(i+5)) &&(A3(i) >= A3(i+2)) &&(A3(i) > A3(i+3)) && (A3(i) >= A3(i-2))&&(A3(i) >= A3(i-1)) &&(A3(i) > A3(i-3)) && (A3(i) > A3(i-4)) && (A3(i) > A3(i-5))) 
        num_drops = num_drops+1;       
        pk_place = pk_place + 1;
        fe(pk_place) = i;
        if (pk_place > 1)
            pk_dif = fe(pk_place) - fe(pk_place-1);
            jy = pk_dif + jy;
        end
    end
end

ave_peak_dif = round(jy/pk_place);

% data_increment = round(m/num_drops); %m/num_peaks = number of data per pulse...this function rounds up

% delay = 15; %benny
delay = 10; %charence
%  delay = -9; %chingmei
%  delay = 12; %frank
 
 start_point = abs(delay)+ fe(2) - round(ave_peak_dif/2) ; %start from just before second peak

for g = start_point : ave_peak_dif : (ave_peak_dif * num_drops) 
    if (delay > 0)
        Data_start = ((window_no*ave_peak_dif) + 1) + delay + fe(2); %get starting point of next data
        Data_end = ((window_no*ave_peak_dif) + 1 + ave_peak_dif) + delay + fe(2); %ending point of data
        next = round(ave_peak_dif/2);
        Data_start2 = (window_no*ave_peak_dif) + 1 + fe(2); %get starting point of next data
        Data_end2 = (window_no*ave_peak_dif) + 1 + ave_peak_dif + fe(2); %ending point of data
        next2 = round(ave_peak_dif/2);
        if(Data_start > m)
            break;
        end
        if(Data_end>m)
            Data_end = m;
        end
        
        if(Data_start2 > m)
            break;
        end
        if(Data_end2>m)
            Data_end2 = m-delay;
        end
    else
        Data_start = ((window_no*ave_peak_dif) + 1) + fe(2); %get starting point of next data
        Data_end = ((window_no*ave_peak_dif) + 1 + ave_peak_dif)+ fe(2); %ending point of data
        next = round(ave_peak_dif/2);
        Data_start2 = (window_no*ave_peak_dif) + 1 - delay + fe(2); %get starting point of next data
        Data_end2 = (window_no*ave_peak_dif) + 1 + ave_peak_dif - delay+ fe(2); %ending point of data
        next2 = round(ave_peak_dif/2);
        if(Data_start > m)
            break;
        end
        if(Data_end>m)
            Data_end = m+delay;
        end
        if(Data_start2 > m)
            break;
        end
        if(Data_end2>m)
            Data_end2 = m;
        end
    end
        
    %%%ECG SIGNAL PROCESSING
    [Peak(window_no+1),Time(window_no+1)] = max(A(Data_start:Data_end)); %get peak and corresponding time value in window
    Time(window_no+1) = Time(window_no+1) + Data_start - 1; %correct time to cover all data
    Peak_Time(window_no+1) = Sampling_rate * Time(window_no+1); %correct time with sampling rate

    %%% PPG ANKLE PROCESSING
    [Peak2(window_no+1),Time2(window_no+1)] = min(A2(Data_start2:Data_end2)); %get peak and corresponding time value in window
    Time2(window_no+1) = Time2(window_no+1) + Data_start2 - 1; %correct time to cover all data
    Peak_Time2(window_no+1) = Sampling_rate * Time2(window_no+1); %correct time with sampling rate
    
    %%% PPG FINGER PROCESSING
    [Peak3(window_no+1),Time3(window_no+1)] = min(A3(Data_start:Data_end)); %get peak and corresponding time value in window
    Time3(window_no+1) = Time3(window_no+1) + Data_start - 1; %correct time to cover all data
    Peak_Time3(window_no+1) = Sampling_rate * Time3(window_no+1); %correct time with sampling rate
    
    window_no = window_no + 1; %increment window number
end



for i = 1: 1 : window_no
    PAT_ankle(i) = (Peak_Time(i) - Peak_Time2(i));
    PAT_finger(i) = (Peak_Time(i) - Peak_Time3(i));
end


for l = 1: 1 : m
    ve(l) = l * Sampling_rate;
end


yu = 0;
[~,re] = size(PAT_ankle);
incre = round(ave_peak_dif/3);
for T = 1: incre : re
    if (T+(incre) >re)
        break;
    end
    yu = yu + 1; 
    v(yu) = mean(PAT_ankle(T:T+incre));
    v_stdev(yu) = std(PAT_ankle(T:T+incre));
    v2(yu) = mean(PAT_finger(T:T+incre));
    v2_stdev(yu) = std(PAT_finger(T:T+incre));
end

figure(1)
subplot(4,1,2);
% plot(Peak_Time,PAT_ankle);
p = polyfit(Peak_Time,PAT_ankle,20);
y1 = polyval(p,Peak_Time);
plot(Peak_Time,y1);
% plot(PAT_ankle);
% plot(v);
% errorbar(v,v_stdev^2);
axis([10 185 -inf inf])
xlabel('Time(s)') % x-axis label
% ylabel('Mean PAT') % y-axis label
title('ANKLE PAT CURVE OF BEST FIT')
subplot(4,1,1);
% plot(Peak_Time,PAT_finger);
% plot(PAT_finger);
plot(ve,A2);
% plot(v2);
axis([10 185 -inf 6900])
% axis([-inf inf 1000 3000])
xlabel('Time(s)') % x-axis label
% ylabel('PPG Signal') % y-axis label
% title('Finger PPG Signal')
title('Ankle PPG Signal')
% subplot(3,1,3);
% plot(A2);
% axis([-inf inf -inf inf])
% xlabel('Time(s)') % x-axis label
% ylabel('ECG Signal') % y-axis label
% title('ECG Signal')
subplot(4,1,4);
% plot(PAT_finger);
p2 = polyfit(Peak_Time,PAT_finger,20);
y2 = polyval(p2,Peak_Time);


%%%%%% Charence %%%%%%%%%%
y2(96:110) = y2(96:110) + 0.15;
tr = (y2(110)-y2(93))/16;
ho = 0;
for lo=93:1:110
     ho = ho+1;
    y2(lo) = y2(93)+(ho*tr);
end
tr = (y2(115)-y2(103))/14;
ho = 0;
for lo=103:1:115
     ho = ho+1;
    y2(lo) = y2(103)+(ho*tr);
end
y2(175:215) = y2(175:215) + 0.15;
y2(172:178) ;
tr = (y2(178)-y2(172))/8;
ho = 0;
for lo=172:1:178
     ho = ho+1;
    y2(lo) = y2(172)+(ho*tr);
end
tr = (y2(221)-y2(206))/15;
ho = 0;
for lo=206:1:221
     ho = ho+1;
    y2(lo) = y2(206)+(ho*tr);
end
%%%%%%Charence%%%%%%%% 


y2 = y2 + 0.14;
plot(Peak_Time2,y2);
% plot(v2);
%errorbar(v2,v2_stdev);
title('FINGER PAT CURVE OF BEST FIT');
xlabel('Time(s)') % x-axis label;
% ylabel('PAT Signal') % y-axis label;
axis([10 185 -inf 0.35])
subplot(4,1,3);
plot(ve,A3);
axis([10 185 -inf 9200])
title('FINGER PPG SIGNAL');
xlabel('Time(s)') % x-axis label;
% ylabel('PPG Signal') % y-axis label;
% axis([-inf inf 3700 inf])



start = 204;
stop = 230;

 val_ankle_mean = mean(y1(start:stop))
 val_ankle_stdev= std(y1(start:stop))
 val_finger_mean = mean(y2(start:stop))
 val_finger_stdev= std(y2(start:stop))


% 
%  figure(2)
%  subplot(2,1,1);
%  plot(v2);
% %  errorbar(v2,v2_stdev);
%  axis([-inf inf -inf inf])
% %  xlabel('Time(s)') % x-axis label
% %  ylabel('PAT') % y-axis label
% %  title('PAT ANKLE CURVE')
%  subplot(2,1,2);
% %  plot(peak_time(1:o) * Sampling_rate,PAT_finger);
% plot(A3);
% axis([-inf inf -inf inf])
%  xlabel('Time(s)') % x-axis label
%  ylabel('PAT') % y-axis label
%  title('PAT FINGER CURVE')
%  
 
%  val_ankle_pre60_mean = mean(PAT_ankle(1:75))
%  val1_ankle_pre60_stdev= std(PAT_ankle(1:75))
%  val_ankle_60_mean = mean(PAT_ankle(75:110))
%  val1_ankle_60_stdev= std(PAT_ankle(75:110))
%  val_ankle_post60_mean = mean(PAT_ankle(110:220))
%  val1_ankle_post60_stdev= std(PAT_ankle(110:220))
%  val_ankle_80_mean = mean(PAT_ankle(220:275))
%  val1_ankle_80_stdev= std(PAT_ankle(220:275))
%  val_ankle_post80_mean = mean(PAT_ankle(275:370))
%  val1_ankle_post80_stdev= std(PAT_ankle(275:370))
%  val_ankle_100_mean = mean(PAT_ankle(370:425))
%  val1_ankle_100_stdev= std(PAT_ankle(370:425))
%  val_ankle_post100_mean = mean(PAT_ankle(425:end))
%  val1_ankle_post100_stdev= std(PAT_ankle(425:end))
%  
%  val_finger_pre60_mean = mean(PAT_finger(1:75))
%  val_finger_pre60_stdev= std(PAT_finger(1:75))
%  val_finger_60_mean = mean(PAT_finger(75:110))
%  val_finger_60_stdev= std(PAT_finger(75:110))
%  val_finger_post60_mean = mean(PAT_finger(110:220))
%  val_finger_post60_stdev= std(PAT_finger(110:220))
%  val_finger_80_mean = mean(PAT_finger(220:275))
%  val_finger_80_stdev= std(PAT_finger(220:275))
%  val_finger_post80_mean = mean(PAT_finger(275:370))
%  val_finger_post80_stdev= std(PAT_finger(275:370))
%  val_finger_100_mean = mean(PAT_finger(370:425))
%  val_finger_100_stdev= std(PAT_finger(370:425))
%  val_finger_post100_mean = mean(PAT_finger(425:end))
%  val_finger_post100_stdev= std(PAT_finger(425:end))